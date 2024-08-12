package com.soybean.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

/**
 * @author soybean
 * @date 2024/8/1 17:48
 * @description
 */
public class ModEventHandler {
    private static final Map<PlayerEntity, Integer> movingPlayers = new HashMap<>();
    private static final int MOVE_DURATION_TICKS = 100; // 5 seconds * 20 ticks/second

    public static void registerEvents() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.getItem() == Items.APPLE) {
                movingPlayers.put(player, MOVE_DURATION_TICKS);
                return TypedActionResult.success(ItemStack.EMPTY);
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });

        ServerTickEvents.END_SERVER_TICK.register(ModEventHandler::onEndServerTick);
    }

    private static void onEndServerTick(MinecraftServer server) {
        movingPlayers.entrySet().removeIf(entry -> {
            PlayerEntity player = entry.getKey();
            int ticksLeft = entry.getValue();
            if (ticksLeft > 0) {
                movePlayerForward(player);
                entry.setValue(ticksLeft - 1);
                return false;
            }
            return true;
        });
    }

    private static void movePlayerForward(PlayerEntity player) {
        Vec3d forwardVec = Vec3d.fromPolar(0, player.getYaw()).normalize().multiply(0.1); // 控制移动速度
        player.setVelocity(forwardVec);
        player.velocityModified = true;
    }
}
