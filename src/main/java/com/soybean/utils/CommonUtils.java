package com.soybean.utils;

import com.soybean.Enchantseries;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author soybean
 * @date 2024/7/27 13:13
 * @description
 */
public class CommonUtils {

    public static final Integer FEET = 0;
    public static final Integer LEGS = 1;
    public static final Integer CHEST = 2;
    public static final Integer HEAD = 3;
    public static final Integer MAIN_HAND = 4;
    public static final Integer OFF_HAND = 5;
    public static final Random RANDOM = new Random();
    public static List<ItemStack> getPlayerInventory(PlayerEntity player){
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(player.getInventory().getArmorStack(0));
        itemStacks.add(player.getInventory().getArmorStack(1));
        itemStacks.add(player.getInventory().getArmorStack(2));
        itemStacks.add(player.getInventory().getArmorStack(3));
        itemStacks.add(player.getMainHandStack());
        itemStacks.add(player.getOffHandStack());
        return itemStacks;
    }

    public static int getEnchantTotalLevel(PlayerEntity player, Enchantment enchantment){
        ItemStack boots = player.getInventory().getArmorStack(0);
        ItemStack leg = player.getInventory().getArmorStack(1);
        ItemStack chest = player.getInventory().getArmorStack(2);
        ItemStack head = player.getInventory().getArmorStack(3);
        return EnchantmentHelper.getLevel(enchantment, boots) + EnchantmentHelper.getLevel(enchantment, leg) + EnchantmentHelper.getLevel(enchantment, chest) + EnchantmentHelper.getLevel(enchantment, head);
    }
}
