package com.soybean.enchantment;

import com.soybean.Enchantseries;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FrostedIceBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @author soybean
 * @date 2024/5/13 14:36
 * @description 附魔 缓速 完成
 */
public class FireShoe extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(FireShoe.class);

    public FireShoe() {
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

    public static void freezeLava(LivingEntity entity, World world, BlockPos blockPos, int level) {
        if (entity.isOnGround()) {
            BlockState blockState = Blocks.MAGMA_BLOCK.getDefaultState();
            int i = Math.min(16, 2 + level);
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            Iterator var7 = BlockPos.iterate(blockPos.add(- i, - 1, - i), blockPos.add(i, - 1, i)).iterator();
            if (level > 1) {
                int x = level - 1;
                Iterator varIterator = BlockPos.iterate(blockPos.add(- x, 0, - i), blockPos.add(x, 0, i)).iterator();
                while (varIterator.hasNext()) {
                    BlockPos rangeBlock = (BlockPos) varIterator.next();
                    if (rangeBlock.isWithinDistance(entity.getPos(), (double) x)) {
                        mutable.set(rangeBlock.getX(), rangeBlock.getY(), rangeBlock.getZ());
                        BlockState state = world.getBlockState(mutable);
                        BlockState state1 = world.getBlockState(mutable.down());
                        if (state.isAir() && ! state1.isAir() && ! state1.isLiquid()) {
                            world.setBlockState(rangeBlock, Blocks.FIRE.getDefaultState());
                        }
                    }
                }
            } else {
                BlockPos pos = entity.getBlockPos();
                BlockPos pos1 = entity.getBlockPos().down();
                BlockState statePlayer = world.getBlockState(pos);
                BlockState state = world.getBlockState(pos1);
                if (! state.isAir() && statePlayer.isAir() && ! state.isLiquid()) {
                    world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                }
            }
            while (var7.hasNext()) {
                BlockPos blockPos2 = (BlockPos) var7.next();
                if (blockPos2.isWithinDistance(entity.getPos(), (double) i)) {
                    mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
                    BlockState blockState2 = world.getBlockState(mutable);
                    if (blockState2.isAir()) {
                        BlockState blockState3 = world.getBlockState(blockPos2);
                        if (blockState3 == Blocks.LAVA.getDefaultState() && blockState.canPlaceAt(world, blockPos2) && world.canPlace(blockState, blockPos2, ShapeContext.absent())) {
                            world.setBlockState(blockPos2, blockState);
                            world.scheduleBlockTick(blockPos2, Blocks.LAVA, MathHelper.nextInt(entity.getRandom(), 60, 120));
                        }
                    }
                }
            }
        }
    }
}
