package com.soybean.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author soybean
 * @date 2024/9/25 17:04
 * @description
 */
public class ExperienceExchangeEnchantment extends Enchantment {

    public static ExperienceExchangeEnchantment Instance;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExperienceExchangeEnchantment.class);

    public ExperienceExchangeEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        Instance = this;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public int getMaxLevel() {
        return 8;
    }

    public void cancelDropToExperience(World world, PlayerEntity player, BlockPos targetBlockPos,int level){
        ServerWorld serverWorld = (ServerWorld) world;
        // 阻止正常的方块破坏行为
        world.setBlockState(targetBlockPos, net.minecraft.block.Blocks.AIR.getDefaultState(), 35);
        int expToDrop = 10; // 示例：每个方块掉落1点经验
        for (int i = 0; i < level; i++) {
            ExperienceOrbEntity.spawn(serverWorld, Vec3d.ofCenter(targetBlockPos), expToDrop);
        }
        //待验证
        ItemStack mainHand = player.getMainHandStack();
        if(mainHand.isDamageable()){
            mainHand.damage(1, player, (p) -> {
                p.sendToolBreakStatus(player.preferredHand);  // 处理工具破损
            });
        }
    }

}
