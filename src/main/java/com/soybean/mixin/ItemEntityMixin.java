package com.soybean.mixin;

import com.soybean.Enchantseries;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author soybean
 * @date 2024/7/27 16:38
 * @description
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemEntityMixin.class);
    @Shadow public abstract void setDespawnImmediately();

    //    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SheepEntity;sheared(Lnet/minecraft/sound/SoundCategory;)V"), method = "interactMob", cancellable = true)
    @Inject(at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/ItemEntity;applyWaterBuoyancy()V"), method = "tick", cancellable = false)
    public void tickWater(CallbackInfo ci) {
        Entity entity = (Entity)(Object) this;
        NbtCompound nbtCompound = new NbtCompound();
        entity.saveNbt(nbtCompound);
        NbtCompound itemTag = nbtCompound.getCompound("Item").getCompound("tag");
        NbtList enchantmentsList = itemTag.getList("Enchantments", NbtElement.COMPOUND_TYPE);
        String id = "";
        int level = 0;
        for (int i = 0; i < enchantmentsList.size(); i++) {
            NbtCompound enchantmentTag = enchantmentsList.getCompound(i);
            id = enchantmentTag.getString("id");// 获取附魔 ID
            level = enchantmentTag.getInt("lvl");// 获取附魔等级
        }
        if(level != 0 && id.equals("enchantseries:feng_dan")){
            this.setDespawnImmediately();
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "tick", cancellable = false)
    public void tick(CallbackInfo ci) {
        ItemEntity entity = (ItemEntity)(Object) this;
        if(entity.getWorld().isRaining() && entity.getWorld().isSkyVisible(entity.getBlockPos())){
            this.setDespawnImmediately();
        }
    }
}
