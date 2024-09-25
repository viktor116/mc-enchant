package com.soybean.enchantment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class SnowSwordEnchantment extends Enchantment {

    public static SnowSwordEnchantment Instance;
    private static final Logger LOGGER = LoggerFactory.getLogger(SnowSwordEnchantment.class);

    public SnowSwordEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        Instance = this;
    }

    public int getMaxLevel() {
        return 8;
    }

    public void BlockToPowderSnow(LivingEntity entity, World world, BlockPos blockPos, int level){
        if(!world.isClient){
            BlockState blockState = Blocks.POWDER_SNOW.getDefaultState();
            int i = level;
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            Iterator var7;
            var7 = BlockPos.iterate(blockPos.add(-i, -Math.min(8,level), -i), blockPos.add(i, 0, i)).iterator();
            while (var7.hasNext()) {
                BlockPos blockPos2 = (BlockPos) var7.next();
                if (blockPos2.isWithinDistance(entity.getPos(), (double) i)) {
                    mutable.set(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
                    BlockState blockState3 = world.getBlockState(blockPos2);
                    if (blockState3 != Blocks.POWDER_SNOW.getDefaultState() && blockState3 != Blocks.AIR.getDefaultState() && blockState.canPlaceAt(world, blockPos2) && world.canPlace(blockState, blockPos2, ShapeContext.absent())) {
                        world.setBlockState(blockPos2, blockState);
                        world.playSound((PlayerEntity)null, blockPos2, SoundEvents.BLOCK_POWDER_SNOW_FALL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        ((ServerWorld)world).spawnParticles(ParticleTypes.SNOWFLAKE, blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(),
                                5,
                                i, 0, i,
                                0.1);
                    }
                }
            }
        }
    }
}
