package com.soybean.networks;

import com.soybean.EnchantseriesClient;
import com.soybean.Interface.DoubleJumpCapable;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author soybean
 * @date 2024/7/29 14:09
 * @description
 */
public class ServerNetworking {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerNetworking.class);
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(
                new Identifier(EnchantseriesClient.MID, "double_jump"),
                (server, player, handler, buf, responseSender) -> {
                    server.execute(() -> {
//                        LOGGER.info("receive the double_jump info...");
//                        ((DoubleJumpCapable)player).tryDoubleJump();
                    });
                }
        );
    }
}
