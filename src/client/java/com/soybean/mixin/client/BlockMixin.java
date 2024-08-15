package com.soybean.mixin.client;

import com.soybean.EnchantseriesClient;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author soybean
 * @date 2024/7/29 11:07
 * @description
 */
@Mixin(Block.class)
public abstract class BlockMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockMixin.class);
    @Inject(method = "onPlaced", at = @At("HEAD"))
    public void onPlaced(World world, BlockPos pos,BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        if (world.isClient) {
            return;
        }
        if (placer != null) {
            ItemStack mainHandStack = placer.getMainHandStack();
            if (EnchantmentHelper.getLevel(EnchantseriesClient.GROWTH_SEEDLINGS_ENCHANTMENT, mainHandStack) > 0) {
                Block block = state.getBlock();
                if (block instanceof CropBlock cropBlock) {
                    world.setBlockState(pos, cropBlock.withAge(cropBlock.getMaxAge()));
                } else if (block instanceof SaplingBlock saplingBlock) {
                    saplingBlock.grow((ServerWorld) world, world.random, pos, state.with(SaplingBlock.STAGE, 1));
                }
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "getSlipperiness", cancellable = true)
    public void getSlipperiness(CallbackInfoReturnable<Float> callback) {
//        LOGGER.info("back ice slipperiness");
//        callback.setReturnValue(Blocks.ICE.getSlipperiness());
    }
}
