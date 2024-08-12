package com.soybean.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author soybean
 * @date 2024/7/27 10:33
 * @description
 */
public class FireRecoverEnchantment extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(FireRecoverEnchantment.class);

    public FireRecoverEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
    }

    public int getMaxLevel() {
        return 3;
    }
    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }
    @Override
    public void onUserDamaged(LivingEntity user, Entity attacker, int level) {
        if (user.isOnFire()) {
            user.extinguish(); // Extinguish the fire
            float healAmount = level * 1.0F; // Heal 2 health points per enchantment level
            user.heal(healAmount);

            // Apply fire resistance effect
            int duration = 100 + (level * 60); // Duration in ticks: 5 seconds + 3 seconds per level
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, duration, 0));
        }
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return true;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return true;
    }

    @Override
    public int getProtectionAmount(int level, DamageSource source) {
        if (source.isIn(DamageTypeTags.IS_FIRE) || source.isOf(DamageTypes.ON_FIRE)) {
            return level * 3;
        }
        return 0;
    }
}
