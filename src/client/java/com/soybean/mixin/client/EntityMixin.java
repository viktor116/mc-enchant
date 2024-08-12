package com.soybean.mixin.client;

import com.soybean.Interface.AirSwimInterface;
import com.soybean.Interface.RambleInterface;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityMixin.class);
    @Shadow protected boolean onGround;

    @Shadow private Vec3d velocity;

    @Inject(at = @At("RETURN"), method = "tick")
    private void onTick(CallbackInfo ci) {
        if (!this.onGround && this.velocity.y <= 0.0D && this.velocity.y > -0.08D) {
            // 玩家达到最高点
            RambleInterface.EVENT.invoker().onJump((Entity)(Object)this);
        }
    }
}
