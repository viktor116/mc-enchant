package com.soybean.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author soybean
 * @date 2024/9/25 17:04
 * @description
 */
public class ExperienceExchangeEnchantment extends Enchantment {

    public static ExperienceExchangeEnchantment Instance;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExperienceExchangeEnchantment.class);

    public ExperienceExchangeEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        Instance = this;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public int getMaxLevel() {
        return 8;
    }
}
