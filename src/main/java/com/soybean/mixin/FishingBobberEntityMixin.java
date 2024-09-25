package com.soybean.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public class FishingBobberEntityMixin {

    @Inject(method = "checkForCollision", at = @At("HEAD"))
    private void onCheckForCollision(CallbackInfo ci) {
//        // 获取鱼鳍的位置
//        BlockPos pos = this.getBlockPos();
//        World world = this.world;
//
//        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
//        if (hitResult.getType() == HitResult.Type.BLOCK) {
//            BlockState state = world.getBlockState(pos);
//            if (!state.isAir()) {
//                // 破坏方块
//                world.breakBlock(pos, true);
//            }
//        }
    }
}
