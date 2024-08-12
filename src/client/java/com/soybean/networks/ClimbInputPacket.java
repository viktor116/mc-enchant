package com.soybean.networks;

import com.soybean.EnchantseriesClient;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * @author soybean
 * @date 2024/8/6 15:35
 * @description
 */
public class ClimbInputPacket {
    public static final Identifier ID = new Identifier(EnchantseriesClient.MID, "climb_input");
    public final boolean climbUp;
    public final boolean climbDown;
    public final float sidewaysInput;

    public ClimbInputPacket(boolean climbUp, boolean climbDown, float sidewaysInput) {
        this.climbUp = climbUp;
        this.climbDown = climbDown;
        this.sidewaysInput = sidewaysInput;
    }

    public static void send(boolean climbUp, boolean climbDown, float sidewaysInput) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(climbUp);
        buf.writeBoolean(climbDown);
        buf.writeFloat(sidewaysInput);
        ClientPlayNetworking.send(ID, buf);
    }

    public static ClimbInputPacket receive(PacketByteBuf buf) {
        return new ClimbInputPacket(buf.readBoolean(), buf.readBoolean(), buf.readFloat());
    }
}
