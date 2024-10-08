package com.soybean.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author soybean
 * @date 2024/10/7 13:26
 * @description 自爆
 */
public class SelfExplosiveEnchantment extends Enchantment {

    public static SelfExplosiveEnchantment Instance;
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubleJumpEnchantment.class);

    public SelfExplosiveEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        Instance = this;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public boolean isCursed() {
        return true;
    }
    public int getMaxLevel() {
        return 8;
    }

    public void activeSelfExplosive(LivingEntity livingEntity,int level) {
        World world = livingEntity.getWorld();
        if(!world.isClient && livingEntity instanceof PlayerEntity player) {
            BlockPos blockPos = player.getBlockPos();
            world.createExplosion(player, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, level, false, World.ExplosionSourceType.TNT);
        }
    }
}
