package com.soybean.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author soybean
 * @date 2024/8/12 12:00
 * @description
 */
public class SniperEnchantment extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(SniperEnchantment.class);

    public SniperEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public int getMaxLevel() {
        return 1;
    }
}
