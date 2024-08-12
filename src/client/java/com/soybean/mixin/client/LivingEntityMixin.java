package com.soybean.mixin.client;

import com.soybean.EnchantseriesClient;
import com.soybean.utils.CommonUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * @author soybean
 * @date 2024/7/27 14:03
 * @description
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Shadow
    protected boolean jumping;
    private static final Logger LOGGER = LoggerFactory.getLogger(LivingEntityMixin.class);

    @Inject(at = @At("RETURN"), method = "damage", cancellable = true)
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getAttacker();
        Object o = this;
        PlayerEntity player = null;
        if(o instanceof PlayerEntity){
            player = (PlayerEntity) o;
        }
        if(player != null){
            List<ItemStack> playerInventory = CommonUtils.getPlayerInventory(player);
            if(attacker instanceof MobEntity mob){
                if (EnchantmentHelper.getLevel(EnchantseriesClient.TO_FRIEND_ENCHANTMENT, playerInventory.get(CommonUtils.FEET)) > 0
                        || EnchantmentHelper.getLevel(EnchantseriesClient.TO_FRIEND_ENCHANTMENT, playerInventory.get(CommonUtils.HEAD)) > 0
                        || EnchantmentHelper.getLevel(EnchantseriesClient.TO_FRIEND_ENCHANTMENT, playerInventory.get(CommonUtils.CHEST)) > 0
                        || EnchantmentHelper.getLevel(EnchantseriesClient.TO_FRIEND_ENCHANTMENT, playerInventory.get(CommonUtils.LEGS)) > 0){
                    if (attacker.isAttackable() && attacker.isLiving()) {
                        List<LivingEntity> nearbyHostiles = attacker.getWorld().getNonSpectatingEntities(LivingEntity.class,
                                attacker.getBoundingBox().expand(7, 5, 7));
                        for (LivingEntity target : nearbyHostiles) {
                            if (target != attacker && target != player) {
                                mob.setTarget(target); // 使用已经转换好的 mobUser
                                mob.setAttacker(target);
                                break;
                            }
                        }
                    }
                    LOGGER.info("player be injured ...");
                }
            }
        }
        LivingEntity self = (LivingEntity) (Object) this;
        int enchantmentLevel = EnchantmentHelper.getEquipmentLevel(EnchantseriesClient.FIRE_RECOVER_ENCHANTMENT, self);

        if (enchantmentLevel > 0 && (source.isIn(DamageTypeTags.IS_FIRE) || source.isOf(DamageTypes.ON_FIRE))) {
            EnchantseriesClient.FIRE_RECOVER_ENCHANTMENT.onUserDamaged(self, source.getAttacker(), enchantmentLevel);
            cir.setReturnValue(false); // Cancel the damage
        }
    }
}
