package com.soybean.networks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * @author soybean
 * @date 2024/8/6 15:37
 * @description
 */
//@Environment(EnvType.CLIENT)
public class ClientInit {
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.player.isClimbing()) {
                boolean climbUp = client.options.forwardKey.isPressed();
                boolean climbDown = client.options.backKey.isPressed();
                float sidewaysInput = client.player.input.movementSideways;
                ClimbInputPacket.send(climbUp, climbDown, sidewaysInput);
            }
        });
    }
}
