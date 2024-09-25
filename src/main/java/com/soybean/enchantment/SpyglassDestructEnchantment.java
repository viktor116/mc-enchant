package com.soybean.enchantment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.logging.Level;

/**
 * @author soybean
 * @date 2024/9/25 15:27
 * @description 远视拆毁
 */
public class SpyglassDestructEnchantment extends Enchantment {
    public static SpyglassDestructEnchantment Instance;
    private static final Logger LOGGER = LoggerFactory.getLogger(SpyglassDestructEnchantment.class);

    public SpyglassDestructEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.VANISHABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        Instance = this;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public int getMaxLevel() {
        return 8;
    }

    public void lookDestruct(PlayerEntity player, int level) {
        BlockHitResult hitResult = (BlockHitResult) player.raycast(300.0D, 0.0F, false);
        BlockPos blockPos = hitResult.getBlockPos();
        World world = player.getWorld();
        int i = level - 1;
        if(!world.isClient && player instanceof ServerPlayerEntity serverPlayer){
            if(level == 1){
                serverPlayer.interactionManager.tryBreakBlock(blockPos);
                world.playSound((PlayerEntity)null, blockPos, SoundEvents.BLOCK_CROP_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
//                BlockState blockState = world.getBlockState(blockPos);
//                ItemStack itemStack = new ItemStack(blockState.getBlock().asItem());
//                ItemScatterer.spawn(world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5,itemStack);
            }else if(level > 1) {
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                Iterator var7;
                var7 = BlockPos.iterate(blockPos.add(-i, -i, -i), blockPos.add(i, i, i)).iterator();
                while (var7.hasNext()) {
                    BlockPos blockPos2 = (BlockPos) var7.next();
                    mutable.set(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
                    BlockState blockState3 = world.getBlockState(blockPos2);
                    if (blockState3 != Blocks.AIR.getDefaultState()) {
                        serverPlayer.interactionManager.tryBreakBlock(blockPos2);
                        world.playSound((PlayerEntity)null, blockPos2, SoundEvents.BLOCK_CROP_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

}
