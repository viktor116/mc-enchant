package com.soybean.mixin.client;

import com.soybean.EnchantseriesClient;
import com.soybean.enchantment.ArrowsRainEnchantment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


/**
 * @author soybean
 * @date 2024/8/7 17:03
 * @description
 * "I" 表示 int 类型
 * "V" 表示 void（无返回值）
 * "L" 开头表示一个类类型，后面跟着完整的类名，以 ";" 结束
 *  F 表示方法返回一个浮点型 (float) 值。
 */

@Mixin(BowItem.class)
public abstract class BowItemMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger(BowItemMixin.class);

    private int tickCounter = 5;

    private boolean isFire = false;
    private int remainNum = 0;
    private boolean listenerRegistered = false;
    private int dynamicRemainingUseTicks;

    @Inject(method = "onStoppedUsing", at = @At("HEAD"))
    private void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        int level = EnchantmentHelper.getLevel(EnchantseriesClient.CONTINUOUS_FIRE_ENCHANTMENT, user.getMainHandStack());
        if (level > 0) {
            remainNum = level;
            isFire = true;
            dynamicRemainingUseTicks = remainingUseTicks;
            if(!listenerRegistered && !world.isClient){
                ServerTickEvents.START_SERVER_TICK.register(listener -> {
                    if(isFire){
                        tickCounter--;
                        if(remainNum > 0 && tickCounter ==0){
                            shoot(stack, world, user, dynamicRemainingUseTicks);
                            remainNum--;
                            tickCounter = 5;
                        }
                        if(remainNum == 0){
                            isFire = false;
                        }
                    }
                });
                listenerRegistered = true;
            }
        }
        //箭雨
        if(EnchantmentHelper.getLevel(EnchantseriesClient.ARROWS_RAIN_ENCHANTMENT, user.getMainHandStack()) > 0){
            ArrowsRainEnchantment.isPlayerArrow = true;
        }
    }

    @Inject(
            method = "onStoppedUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V",
                    shift = At.Shift.AFTER // 将注入点移动到setVelocity调用之后
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void injectCustomArrowVelocity(ItemStack stack, World world, LivingEntity user, int remainingUseTicks,
                                           CallbackInfo ci, PlayerEntity playerEntity, boolean bl, ItemStack itemStack,
                                           int i, float f, boolean bl2, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity) {
        if( EnchantmentHelper.getLevel(EnchantseriesClient.SNIPER_ENCHANTMENT, user.getMainHandStack())>0){
            float speedMultiplier = 10;
            persistentProjectileEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, f * 3.0F * speedMultiplier, 0);
        }
    }

    private void shoot(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        LOGGER.info("user.name={}",user.getName());
        BowItem thisItem = (BowItem) ((Object) this);
        if (user instanceof PlayerEntity playerEntity) {
            boolean bl = playerEntity.getAbilities().creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack itemStack = playerEntity.getProjectileType(stack);
            if (!itemStack.isEmpty() || bl) {
                if (itemStack.isEmpty()) {
                    itemStack = new ItemStack(Items.ARROW);
                }
                int i = thisItem.getMaxUseTime(stack) - remainingUseTicks;
                float f = BowItem.getPullProgress(i);
                if (!((double) f < 0.1)) {
                    boolean bl2 = bl && itemStack.isOf(Items.ARROW);
                    if (!world.isClient) {
                        ArrowItem arrowItem = (ArrowItem) (itemStack.getItem() instanceof ArrowItem ? itemStack.getItem() : Items.ARROW);
                        PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, itemStack, playerEntity);
                        if( EnchantmentHelper.getLevel(EnchantseriesClient.SNIPER_ENCHANTMENT, user.getMainHandStack())>0){
                            float speedMultiplier = 10;
                            persistentProjectileEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, f * 3.0F * speedMultiplier, 0);
                        }else{
                            persistentProjectileEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, f * 3.0F, 1.0F);
                        }
                        if (f == 1.0F) {
                            persistentProjectileEntity.setCritical(true);
                        }

                        int j = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
                        if (j > 0) {
                            persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + (double) j * 0.5 + 0.5);
                        }

                        int k = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
                        if (k > 0) {
                            persistentProjectileEntity.setPunch(k);
                        }

                        if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
                            persistentProjectileEntity.setOnFireFor(100);
                        }

                        stack.damage(1, playerEntity, (p) -> {
                            p.sendToolBreakStatus(playerEntity.getActiveHand());
                        });
                        if (bl2 || playerEntity.getAbilities().creativeMode && (itemStack.isOf(Items.SPECTRAL_ARROW) || itemStack.isOf(Items.TIPPED_ARROW))) {
                            persistentProjectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                        }

                        world.spawnEntity(persistentProjectileEntity);
                    }

                    world.playSound((PlayerEntity) null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!bl2 && !playerEntity.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            playerEntity.getInventory().removeOne(itemStack);
                        }
                    }
                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(thisItem));
                }
            }
        }
    }
    private int calculateDynamicRemainingUseTicks(LivingEntity user) {
        // 你可以基于当前的游戏状态或其他逻辑来动态计算 remainingUseTicks。
        return user.getItemUseTimeLeft();
    }
}
