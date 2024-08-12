package com.soybean.enchantment;

import com.soybean.EnchantseriesClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

//漫步完成

public class RambleEnchantment extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToFriendEnchantment.class);

    private static final List<Block> allItems = Arrays.asList(
            Blocks.RED_CONCRETE,
            Blocks.ORANGE_CONCRETE,
            Blocks.YELLOW_CONCRETE,
            Blocks.LIME_CONCRETE,
            Blocks.GREEN_CONCRETE,
            Blocks.BLUE_CONCRETE,
            Blocks.CYAN_CONCRETE,
            Blocks.LIGHT_BLUE_CONCRETE,
            Blocks.PURPLE_CONCRETE,
            Blocks.MAGENTA_CONCRETE,
            Blocks.PINK_CONCRETE,
            Blocks.WHITE_CONCRETE
    );
    private static final Random RANDOM = new Random();

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public RambleEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    public int getMaxLevel() {
        return 8;
    }

    public static void onJump(Entity entity,int level) {
        World world = entity.getWorld();
        Block block = allItems.get(RANDOM.nextInt(allItems.size()));
        if(level == 1){
            BlockPos blockPos = entity.getBlockPos();
            if(world.getBlockState(blockPos.down()).isAir()){
                world.setBlockState(blockPos.down(), block.getDefaultState());
            }
        }else{
            int i = Math.min(16, 2 + level);
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            Iterator var7 = BlockPos.iterate(entity.getBlockPos().add(- level, -1, - level), entity.getBlockPos().add(level, -1, level)).iterator();
            while (var7.hasNext()) {
                BlockPos blockPos2 = (BlockPos) var7.next();
                if (blockPos2.isWithinDistance(entity.getPos(), (double) i)) {
                    mutable.set(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
                    BlockState blockState2 = world.getBlockState(mutable);
                    if (blockState2.isAir()) {
                        world.setBlockState(blockPos2, block.getDefaultState());
                    }
                }
            }
        }
    }
}
