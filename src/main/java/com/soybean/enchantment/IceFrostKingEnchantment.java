package com.soybean.enchantment;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class IceFrostKingEnchantment extends Enchantment{

    private static final Logger LOGGER = LoggerFactory.getLogger(IceFrostKingEnchantment.class);

    public IceFrostKingEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    public int getMaxLevel() {
        return 8;
    }

    public static void freezeBlock(LivingEntity entity, World world, BlockPos blockPos, int level) {
        if(!world.isClient){
            BlockState blockState = Blocks.ICE.getDefaultState();
            int i = Math.min(16, 2 + level);
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            Iterator var7;
//            int rangeDistant = level / 2;
            if (entity.hasVehicle()) {
                var7 = BlockPos.iterate(blockPos.add(-i, 0, -i), blockPos.add(i, 0, i)).iterator();
            } else {
                var7 = BlockPos.iterate(blockPos.add(-i, -1, -i), blockPos.add(i, -1 , i)).iterator();
            }
            while (var7.hasNext()) {
                BlockPos blockPos2 = (BlockPos) var7.next();
                if (blockPos2.isWithinDistance(entity.getPos(), (double) i)) {
                    mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
                    BlockState blockState2 = world.getBlockState(mutable);
                    if (blockState2.isAir()) {
                        BlockState blockState3 = world.getBlockState(blockPos2);
                        if (blockState3 != Blocks.ICE.getDefaultState() && blockState.canPlaceAt(world, blockPos2) && world.canPlace(blockState, blockPos2, ShapeContext.absent())) {
                            world.setBlockState(blockPos2, blockState);
                        }
                    }
                }
            }
        }
    }
}
