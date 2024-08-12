package com.soybean.enchantment;

import com.soybean.EnchantseriesClient;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author soybean                                                                                                            .................................................+
 * @date 2024/7/24 14:11
 * @description 画地为牢
 */
public class DrawCircleEnchantment extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(DrawCircleEnchantment.class);

    public DrawCircleEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,EquipmentSlot.HEAD});
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public int getMaxLevel() {
        return 2;
    }

    // Register the event listener
    public static void registerListener() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient) return TypedActionResult.pass(ItemStack.EMPTY);
            ItemStack stack = player.getStackInHand(hand);
            if (EnchantmentHelper.getLevel(EnchantseriesClient.DRAW_CIRCLE_ENCHANTMENT, stack) == 1) {
                // Enchantment is active, call the method to place the blocks
                placeRestrictiveBlocks(player);
                return TypedActionResult.fail(ItemStack.EMPTY);
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;
            ItemStack stack = player.getStackInHand(hand);
            if (EnchantmentHelper.getLevel(EnchantseriesClient.DRAW_CIRCLE_ENCHANTMENT, stack) > 1) {
                placeRestrictiveBlocks(world, hitResult.getBlockPos().up());
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }

    private static void placeRestrictiveBlocks(PlayerEntity player) {
        World world = player.getEntityWorld();
        BlockPos playerPos = player.getBlockPos();

        // Place the floor
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                world.setBlockState(playerPos.add(x, -1, z), Blocks.STONE.getDefaultState());
            }
        }

        // Place the ceiling
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                world.setBlockState(playerPos.add(x, 2, z), Blocks.STONE.getDefaultState());
            }
        }

        // Place the walls
        for (int y = 0; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                world.setBlockState(playerPos.add(x, y, -1), Blocks.IRON_BARS.getDefaultState());
                world.setBlockState(playerPos.add(x, y, 1), Blocks.IRON_BARS.getDefaultState());
            }
            for (int z = -1; z <= 1; z++) {
                world.setBlockState(playerPos.add(-1, y, z), Blocks.IRON_BARS.getDefaultState());
                world.setBlockState(playerPos.add(1, y, z), Blocks.IRON_BARS.getDefaultState());
            }
        }
    }

    private static void placeRestrictiveBlocks(World world, BlockPos pos) {
        // Calculate the positions for the cage's structure
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                world.setBlockState(pos.add(x, -1, z), Blocks.STONE.getDefaultState());
                world.setBlockState(pos.add(x, 2, z), Blocks.STONE.getDefaultState());
            }
        }

        for (int y = 0; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                world.setBlockState(pos.add(x, y, -1), Blocks.IRON_BARS.getDefaultState());
                world.setBlockState(pos.add(x, y, 1), Blocks.IRON_BARS.getDefaultState());
            }
            for (int z = -1; z <= 1; z++) {
                world.setBlockState(pos.add(-1, y, z), Blocks.IRON_BARS.getDefaultState());
                world.setBlockState(pos.add(1, y, z), Blocks.IRON_BARS.getDefaultState());
            }
        }
    }

}
