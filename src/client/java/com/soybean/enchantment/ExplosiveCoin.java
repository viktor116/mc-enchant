package com.soybean.enchantment;

import com.soybean.EnchantseriesClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

//爆金币 完成
public class ExplosiveCoin extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToFriendEnchantment.class);

    public static final Registry<Item> registry = Registries.ITEM;

    public static List<Item> allItems = registry.stream().toList();

    public ExplosiveCoin() {
        super(Rarity.UNCOMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }
    public int getMaxLevel() {
        return 1;
    }

    public static void onDamaged(PlayerEntity player, Entity attacker) {
        ItemStack hand = player.getMainHandStack();
        if (EnchantmentHelper.getLevel(EnchantseriesClient.EXPLOSIVE_COIN, hand) > 0 && hand.isDamageable()) {
            //随机在怪物位置生成一个物品
            // 随机选择一个物品，这里假设我们有一个物品列表
//                List<Item> possibleItems = Arrays.asList(
//                        Items.DIAMOND,
//                        Items.GOLD_INGOT,
//                        Items.IRON_INGOT
//                );
            Item randomItem = allItems.get(player.getRandom().nextInt(allItems.size()));
            // 创建ItemEntity并设置位置
            Vec3d pos = attacker.getPos().add(0, 1, 0);
            ItemEntity itemEntity = new ItemEntity(attacker.getWorld(), pos.x, pos.y, pos.z, new ItemStack(randomItem));
            // 设置一些额外的属性，如速度、旋转等
            itemEntity.setToDefaultPickupDelay();
            itemEntity.setVelocity(player.getRandom().nextGaussian() * 0.05, player.getRandom().nextGaussian() * 0.05 + 0.2F, player.getRandom().nextGaussian() * 0.05);
            // 将ItemEntity添加到世界中
            attacker.getWorld().spawnEntity(itemEntity);
        }
    }
}
