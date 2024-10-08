package com.soybean.mixin.method;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

/**
 * @author soybean
 * @date 2024/10/8 10:59
 * @description
 */
public class MultiShot {
    public static boolean loadProjectiles(LivingEntity shooter, ItemStack oneItem) {
        int i = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, oneItem);
        int j = i == 0 ? 1 : 3;
        boolean bl = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).getAbilities().creativeMode;
        ItemStack itemStack = shooter.getProjectileType(oneItem);
        ItemStack itemStack2 = itemStack.copy();

        for(int k = 0; k < j; ++k) {
            if (k > 0) {
                itemStack = itemStack2.copy();
            }

            if (itemStack.isEmpty() && bl) {
                itemStack = new ItemStack(Items.TRIDENT);
                itemStack2 = itemStack.copy();
            }

            if (!loadProjectile(shooter, oneItem, itemStack, k > 0, bl)) {
                return false;
            }
        }

        return true;
    }

    public static boolean loadProjectile(LivingEntity shooter, ItemStack oneItem, ItemStack projectile, boolean simulated, boolean creative) {
        if (projectile.isEmpty()) {
            return false;
        } else {
            boolean bl = creative && projectile.getItem() instanceof ArrowItem;
            ItemStack itemStack;
            if (!bl && !creative && !simulated) {
                itemStack = projectile.split(1);
                if (projectile.isEmpty() && shooter instanceof PlayerEntity) {
                    ((PlayerEntity)shooter).getInventory().removeOne(projectile);
                }
            } else {
                itemStack = projectile.copy();
            }

            putProjectile(oneItem, itemStack);
            return true;
        }
    }

    public static void putProjectile(ItemStack oneItem, ItemStack projectile) {
        NbtCompound nbtCompound = oneItem.getOrCreateNbt();
        NbtList nbtList;
        if (nbtCompound.contains("ChargedProjectiles", 9)) {
            nbtList = nbtCompound.getList("ChargedProjectiles", 10);
        } else {
            nbtList = new NbtList();
        }

        NbtCompound nbtCompound2 = new NbtCompound();
        projectile.writeNbt(nbtCompound2);
        nbtList.add(nbtCompound2);
        nbtCompound.put("ChargedProjectiles", nbtList);
    }

}
