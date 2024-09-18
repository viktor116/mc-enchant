package com.soybean.enchantment;

import com.soybean.Enchantseries;
import com.soybean.utils.CommonUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

//化敌为友 完成

public class ToFriendEnchantment extends Enchantment {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToFriendEnchantment.class);

    public ToFriendEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public int getMaxLevel() {
        return 1;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity attacker, int level) {
        if (user instanceof PlayerEntity player) {
            List<ItemStack> playerInventory = CommonUtils.getPlayerInventory(player);
            ItemStack hand = playerInventory.get(CommonUtils.MAIN_HAND);
            if (EnchantmentHelper.getLevel(Enchantseries.TO_FRIEND_ENCHANTMENT, hand) > 0) {
//                LOGGER.info("user={},attacter={}", user.getClass().getName(), attacker.getClass().getName());
                if (attacker instanceof MobEntity mob) { // 检查 user 是否为 MobEntity
                    if (attacker.isAttackable() && attacker.isLiving()) {
                        List<LivingEntity> nearbyHostiles = attacker.getWorld().getNonSpectatingEntities(LivingEntity.class,
                                attacker.getBoundingBox().expand(9, 5, 9));
                        nearbyHostiles.removeIf(target -> target == user || target == attacker); // 不排除target == target会攻击自己 //todo 这里有趣
                        nearbyHostiles.sort(Comparator.comparingDouble(target -> target.squaredDistanceTo(target))); // 按距离排序
                        if (!nearbyHostiles.isEmpty()) {
                            LivingEntity closestTarget = nearbyHostiles.get(0); // 获取最近的敌对生物
                            if (closestTarget != null) {
                                mob.setTarget(closestTarget); // 使用已经转换好的 mobUser
                                mob.setAttacker(closestTarget);
                            }
                        }
//                        for (LivingEntity target : nearbyHostiles) {
//                            if (target != attacker && target != user) {
//                                mob.setTarget(target); // 使用已经转换好的 mobUser
//                                mob.setAttacker(target);
//                                break;
//                            }
//                        }
                    }
                }
            }
        }
    }
}
