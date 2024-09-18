package com.soybean.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FrostedIceBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

/**
 * @author soybean
 * @date 2024/7/27 12:55
 * @description
 */
@Mixin(FrostWalkerEnchantment.class)
public class FrostWalkerEnchantmentMixin {
    @Shadow
    public static void freezeWater(LivingEntity entity, World world, BlockPos blockPos, int level) {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FrostWalkerEnchantmentMixin.class);

    @Inject(at = @At("HEAD"), method = "freezeWater", cancellable = true)
    private static void freezeWater(LivingEntity entity, World world, BlockPos blockPos, int level, CallbackInfo ci) {
        if(entity instanceof PlayerEntity player){
            if(player.hasVehicle()){
                freezeWaterBoat(entity, entity.getWorld(), new BlockPos((int) entity.getX(), (int)entity.getY(), (int)entity.getZ()), level);
            }
        }
    }

    private static void freezeWaterBoat(Entity entity, World world, BlockPos blockPos, int level){
        BlockState blockState = Blocks.FROSTED_ICE.getDefaultState();
        int i = Math.min(16, 2 + level);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Iterator var7 = BlockPos.iterate(blockPos.add(-i, 0, -i), blockPos.add(i, 0, i)).iterator();
        while(var7.hasNext()) {
            BlockPos blockPos2 = (BlockPos)var7.next();
            if (blockPos2.isWithinDistance(entity.getPos(), (double)i)) {
                mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
                BlockState blockState2 = world.getBlockState(mutable);
                if (blockState2.isAir()) {
                    BlockState blockState3 = world.getBlockState(blockPos2);
                    if (blockState3 == FrostedIceBlock.getMeltedState() && blockState.canPlaceAt(world, blockPos2) && world.canPlace(blockState, blockPos2, ShapeContext.absent())) {
                        world.setBlockState(blockPos2, blockState);
                        world.scheduleBlockTick(blockPos2, Blocks.FROSTED_ICE, 80 * level);
                    }
                }
            }
        }
    }
}
