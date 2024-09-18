package com.soybean.mixin;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.soybean.Enchantseries;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author soybean
 * @date 2024/7/29 11:07
 * @description
 */
@Mixin(Block.class)
public abstract class BlockMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockMixin.class);

    private static List<PlayerEntity> hasFlameFrostList = new ArrayList<>();

    @Inject(method = "onPlaced", at = @At("HEAD"))
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        if (world.isClient) {
            return;
        }
        if (placer != null) {
            ItemStack mainHandStack = placer.getMainHandStack();
            if (EnchantmentHelper.getLevel(Enchantseries.GROWTH_SEEDLINGS_ENCHANTMENT, mainHandStack) > 0) {
                Block block = state.getBlock();
                if (block instanceof CropBlock cropBlock) {
                    world.setBlockState(pos, cropBlock.withAge(cropBlock.getMaxAge()));
                } else if (block instanceof SaplingBlock saplingBlock) {
                    saplingBlock.grow((ServerWorld) world, world.random, pos, state.with(SaplingBlock.STAGE, 1));
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "getSlipperiness", cancellable = true)
    public void getSlipperiness(CallbackInfoReturnable<Float> callback) {
        LOGGER.info("now size="+ hasFlameFrostList.size());
        if(hasFlameFrostList.size() > 0){
            callback.setReturnValue(0.98F); // 冰块滑动系数
        }
//        Block block = ((Block) (Object) this);
//        // 检查是否为岩浆块
//        if (block.getDefaultState().isOf(Blocks.MAGMA_BLOCK)) {
//
//            MinecraftClient client = MinecraftClient.getInstance();
//            PlayerEntity player = client.player;
//
//            // 如果当前玩家存在并且装备了附有 FlameFrostWalker 附魔的靴子
//            if (player != null && hasFlameFrostWalkerEnchantment(player)) {
//                // 设置岩浆块的滑动系数为冰块的滑动系数
//                callback.setReturnValue(0.98F); // 冰块滑动系数
//            }
//        }
    }

    @Inject(at = @At("HEAD"), method = "onSteppedOn", cancellable = true)
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        Block block = ((Block) (Object) this);
        if (block.getDefaultState().isOf(Blocks.MAGMA_BLOCK)) {
            if(entity instanceof PlayerEntity player){
                // 如果当前玩家存在并且装备了附有 FlameFrostWalker 附魔的靴子
                if (player != null && hasFlameFrostWalkerEnchantment(player)) {
                    // 设置岩浆块的滑动系数为冰块的滑动系数
                    hasFlameFrostList.add(player);
                }else {
                    if(hasFlameFrostList.contains(player)){
                        hasFlameFrostList.remove(player);
                    }
                }
            }
        }
    }

    // 检查玩家是否穿着附有 FlameFrostWalker 附魔的靴子
    private boolean hasFlameFrostWalkerEnchantment(PlayerEntity player) {
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        return EnchantmentHelper.getLevel(Enchantseries.FLAME_FROST_WALKER_ENCHANTMENT, boots) > 0;
    }

}
