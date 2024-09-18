package com.soybean.enchantment;

import com.soybean.Enchantseries;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.tick.OrderedTick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author soybean
 * @date 2024/8/15 15:14
 * @description 炎霜行者
 */
public class FlameFrostWalkerEnchantment extends Enchantment {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlameFrostWalkerEnchantment.class);

    public static final Set<BlockPos> customBlockSets = new HashSet<>();
//
//    private static Integer tickNum = 0;
//    private static World WORLD;

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
//            if(tickNum > 60 && !customMagmaBlocks.isEmpty() && WORLD != null){
//                int i = CommonUtils.RANDOM.nextInt(10, 100);
//                LOGGER.info("solution freeze lava");
//                for (int j = 0; j < i; j++) {
//                    BlockPos pos = customMagmaBlocks.poll();
//                    if(pos!=null){
//                        onScheduledTick(WORLD,pos);
//                    }
//                }
//                tickNum = 0;
//            }else {
//                tickNum ++;
//            }
        });
    }

    public static void freezeLava(LivingEntity entity, World world, BlockPos blockPos, int level) {
        BlockState blockState = Blocks.MAGMA_BLOCK.getDefaultState();
        int i = Math.min(16, 2 + level);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Iterator var7;
        if (entity.hasVehicle()) {
            var7 = BlockPos.iterate(blockPos.add(-i, 0, -i), blockPos.add(i, 0, i)).iterator();
        } else {
            var7 = BlockPos.iterate(blockPos.add(-i, -1, -i), blockPos.add(i, -1, i)).iterator();
        }
        while (var7.hasNext()) {
            BlockPos blockPos2 = (BlockPos) var7.next();
            if (blockPos2.isWithinDistance(entity.getPos(), (double) i)) {
                mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
                BlockState blockState2 = world.getBlockState(mutable);
                if (blockState2.isAir()) {
                    BlockState blockState3 = world.getBlockState(blockPos2);
                    if (blockState3 == Blocks.LAVA.getDefaultState() && blockState.canPlaceAt(world, blockPos2) && world.canPlace(blockState, blockPos2, ShapeContext.absent())) {
                        world.setBlockState(blockPos2, blockState);
                        customBlockSets.add(blockPos2);
                        world.scheduleBlockTick(blockPos2, Blocks.MAGMA_BLOCK, MathHelper.nextInt(entity.getRandom(), 120, 240));
//                        scheduleMagmaToLavaConversion(world, blockPos2, entity);
                    }
                }
            }
        }
    }
    private static void scheduleMagmaToLavaConversion(World world, BlockPos pos, LivingEntity entity) {
        int delay = entity.getRandom().nextBetween(120, 240);
//        world.scheduleBlockTick(pos, Blocks.MAGMA_BLOCK, delay);
        world.getBlockTickScheduler().scheduleTick(new OrderedTick<>(Blocks.MAGMA_BLOCK, pos, delay, 0L));
    }
    // 检查玩家是否穿着附有 FlameFrostWalker 附魔的靴子
    private static boolean hasFlameFrostWalkerEnchantment(ServerPlayerEntity player) {
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        return EnchantmentHelper.getLevel(Enchantseries.FLAME_FROST_WALKER_ENCHANTMENT, boots) > 0;
    }

}
