package com.soybean.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author soybean
 * @date 2024/7/29 10:58
 * @description
 */
public class GrowthOfSeedlingsEnchantment extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrowthOfSeedlingsEnchantment.class);

    public GrowthOfSeedlingsEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public int getMaxLevel() {
        return 1;
    }
}
