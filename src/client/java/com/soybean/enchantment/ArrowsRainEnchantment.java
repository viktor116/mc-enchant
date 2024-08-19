package com.soybean.enchantment;

import com.soybean.EnchantseriesClient;
import com.soybean.utils.CommonUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * @author soybean
 * @date 2024/8/19 11:55
 * @description 箭雨
 */
public class ArrowsRainEnchantment extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArrowsRainEnchantment.class);

    public ArrowsRainEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    public int getMaxLevel() {
        return 8;
    }

    public static void shootArrowRain(HitResult hitResult, int level, Entity entity)  {
        //todo 箭雨需要更新
        float f = 1.0F;
        double radius = Math.min(16,level * 2 + 1);
        Vec3d pos = hitResult.getPos();
        ItemStack stack = new ItemStack(Items.ARROW);
        World world = entity.getWorld();
        if (!((double) f < 0.1)) {
            if (!world.isClient && entity instanceof LivingEntity user) {
                ArrowItem arrowItem = (ArrowItem) Items.ARROW;
                double offsetX = Math.cos(CommonUtils.RANDOM.nextDouble()  * Math.PI * 2) * radius;
                double offsetZ = Math.sin(CommonUtils.RANDOM.nextDouble()  * Math.PI * 2) * radius;
                PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, stack, (LivingEntity) user);
                persistentProjectileEntity.setVelocity(pos.getX()+offsetX,pos.getY() + 20,pos.getZ()+offsetZ, f * 3.0F, 0);
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

                world.spawnEntity(persistentProjectileEntity);
            }
            world.playSound((PlayerEntity) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
        }


//        ItemEntity itemEntity = new ItemEntity(player.getWorld(), player.getX(), player.getY(), player.getZ(), new ItemStack(MyModClient.DCHERRYSITEM));
//
//        double offsetX = Math.cos(SunflowerStatusEffect.random.nextDouble()  * Math.PI * 2) * SunflowerStatusEffect.radius;
//        double offsetZ = Math.sin(SunflowerStatusEffect.random.nextDouble()  * Math.PI * 2) * SunflowerStatusEffect.radius;
//        // 设置物品实体的位置
//        itemEntity.setPos(player.getX() + offsetX, player.getY() + player.getStandingEyeHeight() +  10.0D, player.getZ() + offsetZ);
//        // 设置物品实体的初速度
//        itemEntity.setVelocity(new Vec3d(0, 0.2, 0));
//        // 在世界中添加物品实体
//        player.getWorld().spawnEntity(itemEntity);
    }
}
