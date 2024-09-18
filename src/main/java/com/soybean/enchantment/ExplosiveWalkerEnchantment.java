package com.soybean.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author soybean
 * @date 2024/8/6 11:17
 * @description
 */
public class ExplosiveWalkerEnchantment extends Enchantment{
    private static final Logger LOGGER = LoggerFactory.getLogger(ExplosiveWalkerEnchantment.class);

    public ExplosiveWalkerEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }
    public int getMaxLevel() {
        return 8;
    }

}
