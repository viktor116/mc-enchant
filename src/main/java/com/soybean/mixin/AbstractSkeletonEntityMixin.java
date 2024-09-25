package com.soybean.mixin;


import com.soybean.Enchantseries;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntity.class)
public class AbstractSkeletonEntityMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSkeletonEntityMixin.class);

    private int tickCounter = 5;

    private boolean isFire = false;
    private int remainNum = 0;
    private boolean listenerRegistered = false;
    private int dynamicRemainingUseTicks;


    @Inject(at = @At("HEAD"), method = "shootAt")
    public void shootAt(LivingEntity target, float pullProgress, CallbackInfo ci) {
        AbstractSkeletonEntity entity = (AbstractSkeletonEntity) (Object) this;
        if (EnchantmentHelper.getLevel(Enchantseries.CONTINUOUS_FIRE_ENCHANTMENT, entity.getMainHandStack()) > 0) {
            remainNum = EnchantmentHelper.getLevel(Enchantseries.CONTINUOUS_FIRE_ENCHANTMENT, entity.getMainHandStack());
            isFire = true;
            if(!listenerRegistered && !entity.getWorld().isClient){
                ServerTickEvents.START_SERVER_TICK.register(listener -> {
                    if(isFire){
                        tickCounter--;
                        if(remainNum > 0 && tickCounter ==0){
                            shoot(entity,pullProgress);
                            remainNum--;
                            tickCounter = 5;
                        }
                        if(remainNum == 0){
                            isFire = false;
                        }
                    }
                });
                listenerRegistered = true;
            }
        }
    }

    private void shoot(LivingEntity user, float f) {
        ItemStack stack = new ItemStack(Items.ARROW);
        World world = user.getWorld();
        if (!((double) f < 0.1)) {
            if (!world.isClient) {
                ArrowItem arrowItem = (ArrowItem) Items.ARROW;
                PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, stack, user);
                if (EnchantmentHelper.getLevel(Enchantseries.SNIPER_ENCHANTMENT, user.getMainHandStack()) > 0) {
                    float speedMultiplier = 10;
                    persistentProjectileEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, f * 3.0F * speedMultiplier, 0);
                } else {
                    persistentProjectileEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, f * 3.0F, 1.0F);
                }
                if (f == 1.0F) {
                    persistentProjectileEntity.setCritical(true);
                }

                int j = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
                if (j > 0) {
                    persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + (double) j * 0.5 + 0.5);
                }

                int k = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
                if (k > 0) {
                    persistentProjectileEntity.setPunch(k);
                }

                if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
                    persistentProjectileEntity.setOnFireFor(100);
                }

                stack.damage(1, user, (p) -> {
                    p.sendToolBreakStatus(user.getActiveHand());
                });

                world.spawnEntity(persistentProjectileEntity);
            }
            world.playSound((PlayerEntity) null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
        }
    }
}
