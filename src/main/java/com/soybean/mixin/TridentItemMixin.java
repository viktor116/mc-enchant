package com.soybean.mixin;

import com.soybean.mixin.method.MultiShot;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author soybean
 * @date 2024/10/8 10:54
 * @description
 */
@Mixin(TridentItem.class)
public class TridentItemMixin {

    @Inject(method = "onStoppedUsing", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z",
            shift = At.Shift.BEFORE // 将注入点移动到setVelocity调用之后
    ),cancellable = true)
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo callbackInfo) {
        //多重射击
        int level = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, stack);
        PlayerEntity playerEntity = (PlayerEntity) user;
        if(level > 0){
            int projectiles = level + 2; // 3 + 支三叉戟

            for (int i = 0; i < projectiles; i++) {
                TridentEntity tridentEntity = new TridentEntity(world, user, stack);

                // 计算每个三叉戟的发射方向，避免重叠
                float spread = (i - (projectiles - 1) / 2f) * 0.1f; // 简单的发射角度调整
                Vec3d direction = user.getRotationVector().add(spread, 0, 0).normalize();

                // 设置三叉戟的速度和方向
                tridentEntity.setVelocity(direction.x, direction.y, direction.z, 1.5F, 1.0F);

                world.spawnEntity(tridentEntity); // 发射三叉戟
                world.playSoundFromEntity((PlayerEntity)null, tridentEntity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if (!playerEntity.getAbilities().creativeMode) {
                    playerEntity.getInventory().removeOne(stack);
                }
                callbackInfo.cancel();
            }
        }
    }
}
