package com.soybean.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author soybean
 * @date 2024/8/21 16:05
 * @description 冻结
 */
public class ArrowFreezeEnchantment  extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(DestructEnchantment.class);

    public ArrowFreezeEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public int getMaxLevel() {
        return 8;
    }
}
