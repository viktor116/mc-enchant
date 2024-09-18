package com.soybean.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author soybean
 * @date 2024/7/24 11:54
 * @description 狂暴
 */
public class RageEnchantment extends Enchantment{
    private static final Logger LOGGER = LoggerFactory.getLogger(RageEnchantment.class);
    private static final UUID SPEED_MODIFIER_UUID = new UUID(0L, 1L);
    private static final UUID JUMP_MODIFIER_UUID = new UUID(0L, 2L);
    private static final UUID HEALING_MODIFIER_UUID = new UUID(0L, 3L);
    private static final UUID DAMAGE_MODIFIER_UUID = new UUID(0L, 4L);
    private static final float SPEED_MODIFIER_AMOUNT = 0.5F;
    private static final float JUMP_MODIFIER_AMOUNT = 0.5F;
    private static final float HEALING_MODIFIER_AMOUNT = 2.0F; // Amount of health to heal
    private static final float DAMAGE_MODIFIER_AMOUNT = 2.0F; // Additional damage per level


    public RageEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public int getMaxLevel() {
        return 8;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (target instanceof LivingEntity livingTarget) {
//            MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.of("当前生物血量"+livingTarget.getHealth()), false);
//            removeModifier(livingTarget, SPEED_MODIFIER_UUID);
//            removeModifier(livingTarget, JUMP_MODIFIER_UUID);
//            removeModifier(livingTarget, HEALING_MODIFIER_UUID);
//            removeModifier(livingTarget, DAMAGE_MODIFIER_UUID);

//            float additionalDamage = DAMAGE_MODIFIER_AMOUNT * level;
//            livingTarget.damage(livingTarget.getDamageSources().magic(), additionalDamage); // Use a suitable damage source
            livingTarget.heal(HEALING_MODIFIER_AMOUNT * level * 4);
//            removeModifier(livingTarget, HEALING_MODIFIER_UUID);

            livingTarget.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(new EntityAttributeModifier(UUID.randomUUID(), "Rage Healing Boost", HEALING_MODIFIER_AMOUNT * level, EntityAttributeModifier.Operation.ADDITION));

            livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * 30 * level , level + 1));
            livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20 * 30 * level , level + 1));
            livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 30 * level , level + 1));
//            double speedBoost = SPEED_MODIFIER_AMOUNT * level;
//            double jumpBoost = JUMP_MODIFIER_AMOUNT * level;

//            livingTarget.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
//                    .addTemporaryModifier(new EntityAttributeModifier(SPEED_MODIFIER_UUID, "Rage Speed Boost", speedBoost, EntityAttributeModifier.Operation.ADDITION));
//
//            livingTarget.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)
//                    .addTemporaryModifier(new EntityAttributeModifier(JUMP_MODIFIER_UUID, "Rage Jump Boost", jumpBoost, EntityAttributeModifier.Operation.ADDITION));
        }
    }

    private void removeModifier(LivingEntity entity, UUID modifierUUID) {
        entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(modifierUUID);
        entity.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).removeModifier(modifierUUID);
        entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).removeModifier(modifierUUID);
    }

    private void scheduleEffectRemoval(LivingEntity entity, int ticks) {
        MinecraftServer server = entity.getServer();
        if (server != null) {
            server.execute(() -> {
                // Remove all temporary modifiers
                removeModifier(entity, SPEED_MODIFIER_UUID);
                removeModifier(entity, JUMP_MODIFIER_UUID);
                removeModifier(entity, HEALING_MODIFIER_UUID);
                removeModifier(entity, DAMAGE_MODIFIER_UUID);

                // Remove any remaining speed effect
                entity.removeStatusEffect(StatusEffects.SPEED);

                LOGGER.info("Removed all RageEnchantment effects.");
            });
        }
    }

}
