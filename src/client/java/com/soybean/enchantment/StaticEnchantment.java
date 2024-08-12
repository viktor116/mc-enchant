package com.soybean.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//静止 完成

public class StaticEnchantment extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToFriendEnchantment.class);

    public StaticEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.CHEST,EquipmentSlot.FEET,EquipmentSlot.HEAD,EquipmentSlot.LEGS});
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public int getMaxLevel() {
        return 1;
    }

    public static void onSwimming(Entity entity) {

    }
}
