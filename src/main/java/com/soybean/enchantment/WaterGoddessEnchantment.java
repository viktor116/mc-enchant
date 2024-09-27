package com.soybean.enchantment;

import com.soybean.Enchantseries;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @author soybean
 * @date 2024/9/26 10:52
 * @description
 */
public class WaterGoddessEnchantment extends Enchantment {

    public static WaterGoddessEnchantment Instance;
    private static final Logger LOGGER = LoggerFactory.getLogger(WaterGoddessEnchantment.class);

    public WaterGoddessEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.WEARABLE, new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.CHEST, EquipmentSlot.HEAD, EquipmentSlot.LEGS, EquipmentSlot.MAINHAND});
        Instance = this;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public int getMaxLevel() {
        return 8;
    }

    public void BlockToWater(LivingEntity entity, World world, BlockPos blockPos, int level) {
        if (!world.isClient) {
            BlockState blockState = Blocks.WATER.getDefaultState();
            int i = 2 + level;
            int maxY = Math.min(8, i / 2);
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            Iterator var7;
            var7 = BlockPos.iterate(blockPos.add(-i, -maxY, -i), blockPos.add(i, maxY, i)).iterator();
            while (var7.hasNext()) {
                BlockPos blockPos2 = (BlockPos) var7.next();
                if (blockPos2.isWithinDistance(entity.getPos(), (double) i)) {
                    mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
                    BlockState blockState3 = world.getBlockState(blockPos2);
                    if (blockState3 != Blocks.WATER.getDefaultState() && blockState3 != Blocks.AIR.getDefaultState() && blockState.canPlaceAt(world, blockPos2) && world.canPlace(blockState, blockPos2, ShapeContext.absent())) {
                        world.setBlockState(blockPos2, blockState);
                    }
                }
            }
        }
    }

    public void registerListener() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (world.isClient) return ActionResult.PASS;
            ItemStack mainHand = player.getStackInHand(hand);
            int level = EnchantmentHelper.getLevel(Enchantseries.WATER_GODDESS_ENCHANTMENT, mainHand);
            if (level > 0) {
                // 判断物品是否为可以放置的方块 (BlockItem)
                Block block = Blocks.WATER;
                // 检查主手物品是否具有耐久度
                if (mainHand.isDamageable()) {
                    // 减少主手物品的耐久度
                    mainHand.damage(1, player, (p) -> {
                        p.sendToolBreakStatus(hand);  // 处理工具破损
                    });
                    // 将击中的方块替换为 lastStack 中的方块
                    BlockState newState = block.getDefaultState();
                    BlockPos.Mutable mutable = new BlockPos.Mutable();
                    Iterator var7;
                    if (level == 1) {
                        world.setBlockState(pos, newState);
                        // 播放方块放置的音效
                        world.playSound(null, pos, block.getSoundGroup(newState).getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                    } else {
                        int range = level - 1;
                        var7 = BlockPos.iterate(pos.add(-range, Math.max(-8, -range), -range), pos.add(range, Math.min(8, range), range)).iterator();
                        while (var7.hasNext()) {
                            BlockPos blockPos2 = (BlockPos) var7.next();
                            if (blockPos2.isWithinDistance(pos, (double) range)) {
                                mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
                                BlockState blockState3 = world.getBlockState(blockPos2);
                                if (blockState3 != Blocks.AIR.getDefaultState() && newState.canPlaceAt(world, blockPos2) && world.canPlace(newState, blockPos2, ShapeContext.absent())) {
                                    world.setBlockState(blockPos2, newState);
                                    world.playSound(null, pos, block.getSoundGroup(newState).getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                                }
                            }
                        }
                    }
                    // 成功修改方块，返回 ActionResult.SUCCESS
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }
}
