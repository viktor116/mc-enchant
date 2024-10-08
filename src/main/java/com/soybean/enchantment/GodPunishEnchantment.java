package com.soybean.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author soybean
 * @date 2024/10/8 11:36
 * @description 引雷(神罚)
 */
public class GodPunishEnchantment extends Enchantment {

    public static GodPunishEnchantment Instance;

    private static final Logger LOGGER = LoggerFactory.getLogger(GodPunishEnchantment.class);

    public GodPunishEnchantment() {
        super(Rarity.COMMON, EnchantmentTarget.TRIDENT, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        Instance = this;
    }

    public int getMaxLevel() {
        return 1;
    }

    public boolean isTreasure() {
        return true;
    }

    public void createLightNing(TridentEntity tridentEntity){
        LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(tridentEntity.getWorld());
        Vec3d pos = tridentEntity.getPos();
        lightningEntity.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
        tridentEntity.getWorld().spawnEntity(lightningEntity);
    }

}
