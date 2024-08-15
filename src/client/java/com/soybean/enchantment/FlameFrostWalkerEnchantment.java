package com.soybean.enchantment;

import com.soybean.EnchantseriesClient;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author soybean
 * @date 2024/8/15 15:14
 * @description 炎霜行者
 */
public class FlameFrostWalkerEnchantment extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlameFrostWalkerEnchantment.class);
    public FlameFrostWalkerEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }
    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }
    public int getMaxLevel() {
        return 8;
    }

    // 在玩家移动时监听事件
    public static void registerEvents() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                applyFlameFrostEffect(player);
            }
//            for (BoatEntity boat : server.getWorld(World.OVERWORLD).getEntitiesByClass(BoatEntity.class, new Box(-1000, -1000, -1000, 1000, 1000, 1000), Entity::isAlive)) {
//                applyBoatSlidingEffect(boat);
//            }
        });
    }

    private static void applyFlameFrostEffect(ServerPlayerEntity player) {
        if (hasFlameFrostWalkerEnchantment(player)) {
            BlockPos blockPos = player.getBlockPos().down();
            BlockState blockState = player.getWorld().getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (block == Blocks.MAGMA_BLOCK) {
                player.setVelocity(player.getVelocity().multiply(1.5, 1.0, 1.5));
            }

        }
    }

    private static void applyBoatSlidingEffect(BoatEntity boat) {
        BlockPos blockPos = boat.getBlockPos().down();
        BlockState blockState = boat.getWorld().getBlockState(blockPos);
        Block block = blockState.getBlock();

        // 如果船在岩浆块上，将其滑动系数设置为冰块的滑动系数
        if (block == Blocks.MAGMA_BLOCK) {
            boat.setVelocity(boat.getVelocity().multiply(1.2, 1.0, 1.2));
        }
    }

    // 检查玩家是否穿着附有 FlameFrostWalker 附魔的靴子
    private static boolean hasFlameFrostWalkerEnchantment(ServerPlayerEntity player) {
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        return EnchantmentHelper.getLevel(EnchantseriesClient.FLAME_FROST_WALKER_ENCHANTMENT, boots) > 0;
    }

}
