package com.soybean.enchantment;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

//死鱼 完成
public class AirSwimmingEnchantment extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToFriendEnchantment.class);

    public AirSwimmingEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public int getMaxLevel() {
        return 1;
    }

}
