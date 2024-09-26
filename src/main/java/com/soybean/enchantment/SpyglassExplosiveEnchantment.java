package com.soybean.enchantment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @author soybean
 * @date 2024/9/26 11:18
 * @description
 */
public class SpyglassExplosiveEnchantment extends Enchantment {
    public static SpyglassExplosiveEnchantment Instance;
    private static final Logger LOGGER = LoggerFactory.getLogger(SpyglassExplosiveEnchantment.class);

    public SpyglassExplosiveEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.VANISHABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        Instance = this;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public int getMaxLevel() {
        return 8;
    }

    public void productExplosive(PlayerEntity player, int level){
        BlockHitResult hitResult = (BlockHitResult) player.raycast(300.0D, 0.0F, true);
        BlockPos blockPos = hitResult.getBlockPos();
        World world = player.getWorld();
        if(!world.isClient){
            world.createExplosion(player, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, level, false, World.ExplosionSourceType.BLOCK);
        }
    }
}
