package com.soybean.mixin.client;

import com.soybean.EnchantseriesClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author soybean
 * @date 2024/7/27 11:16
 * @description
 */
@Mixin(FlintAndSteelItem.class)
public class FlintAndSteelItemMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlintAndSteelItemMixin.class);
    @Inject(at = @At("HEAD"), method = "useOnBlock", cancellable = true)
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        ItemStack mainHandStack = playerEntity.getInventory().getMainHandStack();
        if(EnchantmentHelper.getLevel(EnchantseriesClient.AMATERASU_ENCHANTMENT,mainHandStack) > 0){
            BlockPos blockPos = context.getBlockPos();
            if(world.isAir(blockPos.up()) && !world.isClient){
                world.playSound(playerEntity, blockPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                world.setBlockState(blockPos.up(), EnchantseriesClient.fire.getDefaultState(),3);
                cir.setReturnValue(ActionResult.FAIL);
            }
        }
    }
}
