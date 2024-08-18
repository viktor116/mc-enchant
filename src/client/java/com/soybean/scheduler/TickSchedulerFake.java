package com.soybean.scheduler;

import com.soybean.enchantment.FlameFrostWalkerEnchantment;
import com.soybean.mixin.client.LivingEntityMixin;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.TickScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
