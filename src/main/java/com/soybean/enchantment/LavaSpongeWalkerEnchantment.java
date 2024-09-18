package com.soybean.enchantment;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author soybean
 * @date 2024/8/19 11:44
 * @description
 */
public class LavaSpongeWalkerEnchantment extends Enchantment{
    private static final Logger LOGGER = LoggerFactory.getLogger(LavaSpongeWalkerEnchantment.class);
    private static final Direction[] DIRECTIONS = Direction.values();

    public LavaSpongeWalkerEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    public int getMaxLevel() {
        return 1;
    }

    public static void updateLava(World world, BlockPos pos,int level) {
        if (absorbLava(world, pos, level)) {
            world.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_SPONGE_ABSORB, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    private static boolean absorbLava(World world, BlockPos pos,int level) {
        return BlockPos.iterateRecursively(pos, Math.max(1,level /2), 65 * level * 10, (currentPos, queuer) -> {
            Direction[] var2 = DIRECTIONS;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Direction direction = var2[var4];
                queuer.accept(currentPos.offset(direction));
            }

        }, (currentPos) -> {
            if (currentPos.equals(pos)) {
                return true;
            } else {
                BlockState blockState = world.getBlockState(currentPos);
                FluidState fluidState = world.getFluidState(currentPos);
                if (!fluidState.isIn(FluidTags.LAVA)) {
                    return false;
                } else {
                    Block block = blockState.getBlock();
                    if (block instanceof FluidDrainable) {
                        FluidDrainable fluidDrainable = (FluidDrainable)block;
                        if (!fluidDrainable.tryDrainFluid((PlayerEntity)null, world, currentPos, blockState).isEmpty()) {
                            return true;
                        }
                    }

                    if (blockState.getBlock() instanceof FluidBlock) {
                        world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), 3);
                    } else {
                        if (!blockState.isOf(Blocks.KELP) && !blockState.isOf(Blocks.KELP_PLANT) && !blockState.isOf(Blocks.SEAGRASS) && !blockState.isOf(Blocks.TALL_SEAGRASS)) {
                            return false;
                        }

                        BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(currentPos) : null;
                        Block.dropStacks(blockState, world, currentPos, blockEntity);
                        world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), 3);
                    }
                    return true;
                }
            }
        }) > 1;
    }
}
