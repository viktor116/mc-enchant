package com.soybean.enchantment;

import com.soybean.Enchantseries;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @author soybean
 * @date 2024/9/25 14:05
 * @description
 */
public class SpyglassLightningEnchantment extends Enchantment {

    public static SpyglassLightningEnchantment Instance;
    private static final Logger LOGGER = LoggerFactory.getLogger(SpyglassLightningEnchantment.class);

    public SpyglassLightningEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.VANISHABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        Instance = this;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public int getMaxLevel() {
        return 8;
    }

    public void lookLightning(PlayerEntity player,int level){
        BlockHitResult hitResult = (BlockHitResult) player.raycast(120.0D, 0.0F, false);
        BlockPos blockPos = hitResult.getBlockPos();
        World world = player.getWorld();
        int i = level - 1;
        if(!world.isClient){
            if(level == 1){
                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
                lightningEntity.refreshPositionAndAngles(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0.0F, 0.0F);
                world.spawnEntity(lightningEntity);
            }else if(level > 1){
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                Iterator var7;
                var7 = BlockPos.iterate(blockPos.add(-i, -Math.min(8,level), -i), blockPos.add(i, 0, i)).iterator();
                while (var7.hasNext()) {
                    BlockPos blockPos2 = (BlockPos) var7.next();
                    mutable.set(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
                    BlockState blockState3 = world.getBlockState(blockPos2);
                    if (blockState3 != Blocks.AIR.getDefaultState() && world.getBlockState(blockPos2.up()) == Blocks.AIR.getDefaultState()) {
                        LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
                        lightningEntity.refreshPositionAndAngles(blockPos2.getX() + 0.5, blockPos2.getY(), blockPos2.getZ() + 0.5, 0.0F, 0.0F);
                        world.spawnEntity(lightningEntity);
                    }
                }
            }
        }
    }
}
