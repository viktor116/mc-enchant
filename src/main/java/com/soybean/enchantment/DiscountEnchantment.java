package com.soybean.enchantment;

import com.soybean.Enchantseries;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.village.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author soybean
 * @date 2024/8/8 16:32
 * @description
 * /summon minecraft:villager ~ ~ ~ {VillagerData:{profession:librarian,level:2,type:plains}}
 * profession：村民的职业，例如 farmer（农民），librarian（图书管理员）。
 * level：村民的职业级别，从1到5。
 * type：村民的类型，例如 plains（平原村民）。
 * /summon minecraft:villager ~ ~ ~ {VillagerData:{profession:farmer,level:2,type:plains},Offers:{Recipes:[{buy:{id:minecraft:wheat,Count:20},sell:{id:minecraft:emerald,Count:1}}]}}
 */
public class DiscountEnchantment extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscountEnchantment.class);

    public DiscountEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public int getMaxLevel() {
        return 1;
    }

    public static void registerEvents() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient && entity instanceof Merchant) {
                ItemStack weapon = player.getMainHandStack();
                if (EnchantmentHelper.getLevel(Enchantseries.DISCOUNT_ENCHANTMENT, weapon) > 0) {
                    removeGossip(player, (Merchant) entity);
                    applyDiscount((Merchant) entity,0);
                    ((ServerWorld) world).getServer().execute(() -> {
                        removeGossip(player, (VillagerEntity) entity);
                    });
                }
                //快速进货
                if (EnchantmentHelper.getLevel(Enchantseries.FAST_STOCK_ENCHANTMENT, weapon) > 0) {
                    applyDiscount((Merchant) entity,1);
                }
            }
            return ActionResult.PASS;
        });

    }

    private static void applyDiscount(Merchant merchant,Integer type) {
        TradeOfferList offers = merchant.getOffers();
        for (TradeOffer offer : offers) {
            if(type == 0) {
                int currentPrice = offer.getAdjustedFirstBuyItem().getCount();
                int newPrice = Math.max(currentPrice - 1, 1); // 每次减少1，最低价格为1
                // 重新创建交易，并设置新的价格
                offer.getOriginalFirstBuyItem().setCount(newPrice);
            }
            if(type == 1){ //重置机会次数
                LOGGER.info("start reset uses");
                offer.resetUses();
            }
        }
    }

    private static void removeGossip(PlayerEntity player, Merchant merchant) {
        VillagerGossips gossip = ((VillagerEntity) merchant).getGossip();
        gossip.remove(player.getUuid(), VillageGossipType.MINOR_NEGATIVE);
        gossip.remove(player.getUuid(), VillageGossipType.MAJOR_NEGATIVE);
    }
}
