package com.soybean.scheduler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TickSchedulerFake implements ServerTickEvents.EndTick{

    private static final Logger LOGGER = LoggerFactory.getLogger(TickSchedulerFake.class);
    @Override
    public void onEndTick(MinecraftServer server) {
//        Set<BlockPos> customBlockSets = FlameFrostWalkerEnchantment.customBlockSets;
//        Set<BlockPos> toRemove = new HashSet<>();
//
//        for (ServerWorld world : server.getWorlds()) {
//            LOGGER.info("Running TickSchedulerFake for dimension: " + world.getRegistryKey().getValue());
//
//            for (BlockPos pos : customBlockSets) {
//                if (world.getBlockState(pos).isOf(Blocks.MAGMA_BLOCK)) {
//                    world.setBlockState(pos, Blocks.LAVA.getDefaultState());
//                    toRemove.add(pos);
//                }
//            }
//        }
//
//        customBlockSets.removeAll(toRemove);
    }
}
