package com.soybean.enchantment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class BlueIceBodyEnchantment extends Enchantment{
    private static final Logger LOGGER = LoggerFactory.getLogger(IceFrostKingEnchantment.class);

    public BlueIceBodyEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public int getMaxLevel() {
        return 8;
    }

    public static void freezeRange(LivingEntity entity, World world, BlockPos blockPos, int level) {
        if(!world.isClient){
            BlockState blockState = Blocks.BLUE_ICE.getDefaultState();
            int i = 2 + level;
            int maxY = Math.min(8 , i / 2);
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            Iterator var7;
            var7 = BlockPos.iterate(blockPos.add(-i, - maxY, -i), blockPos.add(i, maxY, i)).iterator();
            while (var7.hasNext()) {
                BlockPos blockPos2 = (BlockPos) var7.next();
                if (blockPos2.isWithinDistance(entity.getPos(), (double) i)) {
                    mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
                    BlockState blockState3 = world.getBlockState(blockPos2);
                    if (blockState3 != Blocks.BLUE_ICE.getDefaultState() && blockState3 != Blocks.AIR.getDefaultState() && blockState.canPlaceAt(world, blockPos2) && world.canPlace(blockState, blockPos2, ShapeContext.absent())) {
                        world.setBlockState(blockPos2, blockState);
                    }
                }
            }
        }
    }


}
