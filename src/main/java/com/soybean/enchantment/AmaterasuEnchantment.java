package com.soybean.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author soybean
 * @date 2024/7/24 12:12
 * @description 天照
 */
public class AmaterasuEnchantment extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmaterasuEnchantment.class);

    public AmaterasuEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }

    public int getMaxLevel() {
        return 3;
    }
}
