package com.soybean.mixin.client;

import com.soybean.Enchantseries;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author soybean
 * @date 2024/7/29 14:06
 * @description
 */
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Unique
    private boolean wantsToDoubleJump = false;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onTickMovement(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity)(Object)this;
        if (!player.isOnGround() && player.input.jumping && !player.input.sneaking) {
            wantsToDoubleJump = true;
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("TAIL"))
    private void onSendMovementPackets(CallbackInfo ci) {
        if (wantsToDoubleJump) {
            ClientPlayNetworking.send(new Identifier(Enchantseries.MID, "double_jump"), PacketByteBufs.empty());
            wantsToDoubleJump = false;
        }
    }
}
