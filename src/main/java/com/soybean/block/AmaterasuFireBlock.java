package com.soybean.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

/**
 * @author soybean
 * @date 2024/7/26 13:57
 * @description
 */
public class AmaterasuFireBlock extends AbstractFireBlock {
    public static final MapCodec<AmaterasuFireBlock> CODEC = createCodec(AmaterasuFireBlock::new);

    public MapCodec<AmaterasuFireBlock> getCodec() {
        return CODEC;
    }

    public AmaterasuFireBlock(AbstractBlock.Settings settings) {
        super(settings, 10.0F);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return this.canPlaceAt(state, world, pos) ? this.getDefaultState() : Blocks.AIR.getDefaultState();
    }

//    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
//        return isSoulBase(world.getBlockState(pos.down()));
//    }
//    public static boolean isSoulBase(BlockState state) {
//        return true;
//        state.isIn(BlockTags.SOUL_FIRE_BASE_BLOCKS);
//    }

    protected boolean isFlammable(BlockState state) {
        return true;
    }
}
