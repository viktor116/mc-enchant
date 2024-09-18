package com.soybean.event;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class MagmaTickHandler {
    public static void onMagmaTick(ServerWorld world, BlockPos pos, BlockState state) {
        if (state.isOf(Blocks.MAGMA_BLOCK)) {
            world.setBlockState(pos, Blocks.LAVA.getDefaultState());
        }
    }
}
