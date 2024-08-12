package com.soybean.enchantment;

import com.soybean.EnchantseriesClient;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlimeBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author soybean
 * @date 2024/7/29 15:19
 * @description
 */
public class SlimeFeetEnchantment extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlimeFeetEnchantment.class);

    public SlimeFeetEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    public int getMaxLevel() {
        return 1;
    }

    private static final Map<UUID, Double> playerFallSpeeds = new HashMap<>();

    public static void registerEvents() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (hasSlimeFeetEnchantment(player)) {
                    applySlimeEffect(player);
                }
            }
        });
    }

    private static boolean hasSlimeFeetEnchantment(ServerPlayerEntity player) {
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        return EnchantmentHelper.getLevel(EnchantseriesClient.SLIME_FEET_ENCHANTMENT, boots) > 0;
    }

    private static void applySlimeEffect(ServerPlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        BlockPos pos = player.getBlockPos().down();
        BlockState state = player.getWorld().getBlockState(pos);
        UUID playerId = player.getUuid();

        // If player is falling, store their fall speed
        if (!player.isOnGround() && velocity.y < 0) {
            playerFallSpeeds.put(playerId, Math.min(velocity.y, playerFallSpeeds.getOrDefault(playerId, 0.0)));
            return;
        }

        // If player has just landed
        if (player.isOnGround() && !(state.getBlock() instanceof SlimeBlock) && playerFallSpeeds.containsKey(playerId)) {
            double fallSpeed = playerFallSpeeds.get(playerId);

            if (fallSpeed < -0.1) {
                double bounceStrength = -fallSpeed * 0.8;
                player.setVelocity(velocity.x, bounceStrength, velocity.z);
                player.velocityModified = true;
                player.fallDistance = 0f;

//                LOGGER.info("Slime feet effect applied. Fall speed: " + fallSpeed + ", New velocity: " + player.getVelocity());
            }

            // Clear the stored fall speed
            playerFallSpeeds.remove(playerId);
        }
    }
}
