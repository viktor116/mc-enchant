package com.soybean.enchantment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @author soybean
 * @date 2024/7/24 12:00
 * @description 水之行者
 */

public class WaterWalker extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(WaterWalker.class);

    public WaterWalker() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    public int getMinPower(int level) {
        return level * 10;
    }

    public int getMaxPower(int level) {
        return this.getMinPower(level) + 15;
    }

    public int getMaxLevel() {
        return 8;
    }

    public static void walkLand(LivingEntity entity, World world, BlockPos blockPos, int level) {
        BlockState blockState = Blocks.WATER.getDefaultState();
        int i = Math.min(16, 2 + level);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        LOGGER.info("velocity = {}",entity.getVelocity().y);
        if(entity.getVelocity().y < -0.1){
            Vec3d posEntity = entity.getPos();
            BlockPos blockPos1 = new BlockPos((int)posEntity.getX(), (int)posEntity.getY(), (int)posEntity.getZ());
            while (blockPos1.getY() > entity.getY() - 20 && entity.getWorld().getBlockState(blockPos).isAir()) {
                blockPos1 = blockPos1.down();
            }
            if(!entity.getWorld().getBlockState(blockPos1).isAir()){
                blockPos = blockPos1.up();
//                LOGGER.info("blockPos1.up = {}",blockPos1);
            }
        }
        if (level > 1) {
            int x = level - 1;
            Iterator varIterator = BlockPos.iterate(blockPos.add(-x, 0, -i), blockPos.add(x, 0, i)).iterator();
            while (varIterator.hasNext()) {
                BlockPos rangeBlock = (BlockPos) varIterator.next();
                if (rangeBlock.isWithinDistance(blockPos, (double) x)) {
                    mutable.set(rangeBlock.getX(), rangeBlock.getY(), rangeBlock.getZ());
                    BlockState state = world.getBlockState(mutable);
                    BlockState state1 = world.getBlockState(mutable.down());
                    if (state.isAir() && !state1.isAir() && !state1.isLiquid()) {
                        world.setBlockState(rangeBlock, blockState);
                    }
                }
            }
        } else {
            BlockPos pos = blockPos;
            BlockPos pos1 = blockPos.down();
            BlockState statePlayer = world.getBlockState(pos);
            BlockState state = world.getBlockState(pos1);
            if (!state.isAir() && statePlayer.isAir() && !state.isLiquid()) {
                world.setBlockState(pos, blockState);
            }
        }
    }
}
