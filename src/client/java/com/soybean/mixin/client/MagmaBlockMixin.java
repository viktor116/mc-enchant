package com.soybean.mixin.client;

import com.soybean.enchantment.FlameFrostWalkerEnchantment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MagmaBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagmaBlock.class)
public class MagmaBlockMixin {
    @Inject(method = "scheduledTick", at = @At("HEAD"))
    private void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        // 调用自定义逻辑来处理附魔生成的MagmaBlock
//        if (world.getBlockState(pos).isOf(Blocks.MAGMA_BLOCK)) {
//            world.setBlockState(pos, Blocks.LAVA.getDefaultState());
//        }
    }
}
