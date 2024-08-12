package com.soybean.network;

import com.soybean.Enchantseries;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * @author soybean
 * @date 2024/8/6 15:35
 * @description
 */
public class ClimbInputPacket {
    public static final Identifier ID = new Identifier(Enchantseries.MID, "climb_input");
    public final boolean climbUp;
    public final boolean climbDown;
    public final float sidewaysInput;

    public ClimbInputPacket(boolean climbUp, boolean climbDown, float sidewaysInput) {
        this.climbUp = climbUp;
        this.climbDown = climbDown;
        this.sidewaysInput = sidewaysInput;
    }

    public static ClimbInputPacket receive(PacketByteBuf buf) {
        return new ClimbInputPacket(buf.readBoolean(), buf.readBoolean(), buf.readFloat());
    }
}
