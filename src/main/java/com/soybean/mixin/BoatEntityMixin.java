package com.soybean.mixin;

import net.minecraft.entity.vehicle.BoatEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author soybean
 * @date 2024/7/27 14:41
 * @description
 */
@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin {
    @Shadow protected abstract boolean checkBoatInWater();
    private static final Logger LOGGER = LoggerFactory.getLogger(BoatEntityMixin.class);
    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void tick(CallbackInfo ci) {
//        if(this.checkBoatInWater()){
            //储存在itemstack之中
//            NbtCompound nbtCompound = new NbtCompound();
//            Entity entity = (Entity)(Object) this;
//            entity.saveNbt(nbtCompound);
//            LOGGER.info("nbtCompound: " + nbtCompound);
//            NbtCompound itemTag = nbtCompound.getCompound("Item").getCompound("tag");
//            NbtList enchantmentsList = itemTag.getList("Enchantments", NbtElement.COMPOUND_TYPE);
//            String id = "";
//            int level = 0;
//            for (int i = 0; i < enchantmentsList.size(); i++) {
//                NbtCompound enchantmentTag = enchantmentsList.getCompound(i);
//                id = enchantmentTag.getString("id");// 获取附魔 ID
//                level = enchantmentTag.getInt("lvl");// 获取附魔等级
//                LOGGER.info("Enchantment ID: " + id + ", Level: " + level);
//            }
//            if(level != 0 && !id.equals("frost_walker")){
//                if(level > 1){
//                    freezeWaterBoat(entity, entity.getWorld(), new BlockPos((int) entity.getX(), (int)entity.getY(), (int)entity.getZ()), level);
//                }
//            }
//        }
    }
}
