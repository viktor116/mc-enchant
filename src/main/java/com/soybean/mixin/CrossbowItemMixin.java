package com.soybean.mixin;

import com.soybean.Enchantseries;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author soybean
 * @date 2024/8/8 15:24
 * @description
 */
@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
    @Shadow private boolean charged;
    private static final Logger LOGGER = LoggerFactory.getLogger(CrossbowItemMixin.class);

    private static int tickCounter = 5;

    private static boolean isFire = false;
    private static int remainNum = 0;

    private static boolean listenerRegistered = false;

    private static Hand dynamicHand = null;

    private static ItemStack dynamicProjectile = null;
    private static ItemStack dynamicItemStack= null;

    private static LivingEntity dynamicShooter = null;
    private static float dynamicSimulated = 0;
    @Shadow
    private static PersistentProjectileEntity createArrow(World world, LivingEntity entity, ItemStack crossbow, ItemStack arrow) {
        ArrowItem arrowItem = (ArrowItem)(arrow.getItem() instanceof ArrowItem ? arrow.getItem() : Items.ARROW);
        PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, arrow, entity);
        if (entity instanceof PlayerEntity) {
            persistentProjectileEntity.setCritical(true);
        }

        persistentProjectileEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
        persistentProjectileEntity.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getLevel(Enchantments.PIERCING, crossbow);
        if (i > 0) {
            persistentProjectileEntity.setPierceLevel((byte)i);
        }

        return persistentProjectileEntity;
    }


    @Inject(method = "shoot", at = @At("TAIL"))
    private static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated, CallbackInfo ci){
        int level = EnchantmentHelper.getLevel(Enchantseries.CONTINUOUS_FIRE_ENCHANTMENT, shooter.getMainHandStack());
        dynamicHand = hand;
        dynamicItemStack = crossbow;
        dynamicProjectile = projectile;
        dynamicShooter = shooter;
        dynamicSimulated = simulated;
        if (level > 0) {
            remainNum = level;
            isFire = true;
            if(!listenerRegistered && !world.isClient) {
                ServerTickEvents.START_SERVER_TICK.register(listener -> {
                    if (isFire) {
                        tickCounter--;
                        if (remainNum > 0 && tickCounter == 0) {
                            startShoot(world, dynamicShooter, dynamicHand, dynamicItemStack, dynamicProjectile, soundPitch, creative, speed, divergence, dynamicSimulated, ci);
                            remainNum--;
                            tickCounter = 5;
                        }
                        if (remainNum == 0) {
                            isFire = false;
                        }
                    }
                });
                listenerRegistered = true;
            }
        }
    }

    private static void startShoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated, CallbackInfo ci) {
        if (!world.isClient) {
            boolean isFirework = projectile.isOf(Items.FIREWORK_ROCKET);
            int multishotLevel = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, crossbow);

            // 根据多重射击附魔等级处理
            int arrowCount = multishotLevel > 0 ? 3 : 1; // 多重射击附魔时射出三支箭

            for (int i = 0; i < arrowCount; i++) {
                Object projectileEntity;
                if (isFirework) {
                    projectileEntity = new FireworkRocketEntity(world, projectile, shooter, shooter.getX(), shooter.getEyeY() - 0.15000000596046448, shooter.getZ(), true);
                } else {
                    projectileEntity = createArrow(world, shooter, crossbow, projectile);
                    if (creative || simulated != 0.0F) {
                        ((PersistentProjectileEntity) projectileEntity).pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                    }
                }

                if (shooter instanceof CrossbowUser crossbowUser) {
                    crossbowUser.shoot(crossbowUser.getTarget(), crossbow, (ProjectileEntity) projectileEntity, simulated);
                } else {
                    Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
                    Quaternionf quaternionf = (new Quaternionf()).setAngleAxis((double)(simulated * 0.017453292F), vec3d.x, vec3d.y, vec3d.z);
                    Vec3d vec3d2 = shooter.getRotationVec(1.0F);
                    Vector3f vector3f = vec3d2.toVector3f().rotate(quaternionf);

                    if (arrowCount == 3) { // 如果是多重射击，调整箭的角度
                        Vector3f axis = new Vector3f(0.0F, 1.0F, 0.0F); // Y轴向量
                        if (i == 1) {
                            vector3f.rotateAxis(-10.0F * 0.017453292F, axis.x(), axis.y(), axis.z()); // 左箭
                        } else if (i == 2) {
                            vector3f.rotateAxis(10.0F * 0.017453292F, axis.x(), axis.y(), axis.z()); // 右箭
                        }
                    }

                    ((ProjectileEntity) projectileEntity).setVelocity((double) vector3f.x(), (double) vector3f.y(), (double) vector3f.z(), speed, divergence);
                }

                crossbow.damage(isFirework ? 3 : 1, shooter, (e) -> {
                    e.sendToolBreakStatus(hand);
                });
                world.spawnEntity((Entity) projectileEntity);
                world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, soundPitch);
            }
        }
    }
}
