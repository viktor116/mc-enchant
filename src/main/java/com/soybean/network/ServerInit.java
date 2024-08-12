package com.soybean.network;

import com.soybean.Enchantseries;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author soybean
 * @date 2024/8/6 15:39
 * @description
 */
public class ServerInit {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerInit.class);
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(
                new Identifier(Enchantseries.MID, "climb_input"),
                (server, player, handler, buf, responseSender) -> {
                    server.execute(() -> {
                        LOGGER.info("receive the climb_input info...");
//                        ((DoubleJumpCapable)player).tryDoubleJump();
                    });
                }
        );
    }
}