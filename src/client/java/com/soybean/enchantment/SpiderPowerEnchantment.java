package com.soybean.enchantment;

import com.soybean.EnchantseriesClient;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author soybean
 * @date 2024/7/24 12:20
 * @description 蜘蛛之力
 */
public class SpiderPowerEnchantment extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToFriendEnchantment.class);

    public SpiderPowerEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public int getMaxLevel() {
        return 8;
    }

    public static void registerEvents() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (isSpiderPowerActive(player)) {
                    enableWallClimbing(player);
                }
            }
        });
    }

    private static boolean isSpiderPowerActive(ServerPlayerEntity player) {
        ItemStack chestArmor = player.getEquippedStack(EquipmentSlot.CHEST);
        return EnchantmentHelper.getLevel(EnchantseriesClient.SPIDER_POWER_ENCHANTMENT, chestArmor) > 0;
    }

    private static void enableWallClimbing(ServerPlayerEntity player) {
        LOGGER.info("isNextToWall(player)={},player.isSneaking()={}",isNextToWall(player),player.isSneaking());
        if (isNextToWall(player) && !player.isSneaking()) {
            player.fallDistance = 0;
            player.setVelocity(player.getVelocity().x, 0.5, player.getVelocity().z);
            player.velocityModified= true; // 确保速度更新
            LOGGER.info("Player is climbing the wall...");
        }
    }

    private static boolean isNextToWall(ServerPlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        World world = player.getWorld();
        Box playerBoundingBox = player.getBoundingBox();

        // 检查四个方向的方块并计算距离
        BlockPos[] directions = {
                pos.north(), pos.south(), pos.east(), pos.west()
        };

        for (BlockPos direction : directions) {
            if (world.getBlockState(direction).isSolidBlock(world, direction)) {
                Box blockBoundingBox = world.getBlockState(direction).getCollisionShape(world, direction).getBoundingBox().offset(direction);
                if (playerBoundingBox.minX <= blockBoundingBox.maxX + 0.5 && playerBoundingBox.maxX >= blockBoundingBox.minX - 0.5 &&
                        playerBoundingBox.minY <= blockBoundingBox.maxY + 0.5 && playerBoundingBox.maxY >= blockBoundingBox.minY - 0.5 &&
                        playerBoundingBox.minZ <= blockBoundingBox.maxZ + 0.5 && playerBoundingBox.maxZ >= blockBoundingBox.minZ - 0.5) {
                    return true;
                }
            }
        }

        return false;
    }
}
