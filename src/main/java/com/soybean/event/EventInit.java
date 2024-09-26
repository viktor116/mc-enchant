package com.soybean.event;

import com.soybean.Enchantseries;
import com.soybean.Interface.RambleInterface;
import com.soybean.damage.FengdanDamage;
import com.soybean.enchantment.*;
import com.soybean.scheduler.TickSchedulerFake;
import com.soybean.utils.CommonUtils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static net.fabricmc.fabric.api.event.world.WorldTickCallback.*;

/**
 * @author soybean
 * @date 2024/7/19 11:11
 * @description
 */
public class EventInit {

    private static final Logger LOGGER = LoggerFactory.getLogger(Enchantseries.class);

    private static final Random random = new Random();

    public static void start() {
        EVENT.register((listener) -> {
            List<? extends PlayerEntity> players = listener.getPlayers();
            for (PlayerEntity player : players) {
                ItemStack boots = player.getInventory().getArmorStack(0);
                ItemStack leg = player.getInventory().getArmorStack(1);
                ItemStack chest = player.getInventory().getArmorStack(2);
                ItemStack head = player.getInventory().getArmorStack(3);
                //水之女神
                int waterGoddessLevel = CommonUtils.getEnchantTotalLevel(player, Enchantseries.WATER_GODDESS_ENCHANTMENT);
                if(waterGoddessLevel > 0 ){
                    WaterGoddessEnchantment.Instance.BlockToWater(player, player.getWorld(), player.getBlockPos(), waterGoddessLevel);
                }
                // 蓝冰圣体
                int blueIceBodyLevel = CommonUtils.getEnchantTotalLevel(player, Enchantseries.ICE_BLUE_BODY_ENCHANTMENT);
                if (blueIceBodyLevel > 0) {
                    BlueIceBodyEnchantment.freezeRange(player, player.getWorld(), player.getBlockPos(), blueIceBodyLevel);
                }
                // 检查靴子是否有特定的附魔 冰霜王者
                if (EnchantmentHelper.getLevel(Enchantseries.ICE_FROST_KING_ENCHANTMENT, boots) > 0) {
                    IceFrostKingEnchantment.freezeBlock(player, player.getWorld(), player.getBlockPos(), EnchantmentHelper.getLevel(Enchantseries.ICE_FROST_KING_ENCHANTMENT, boots));
                }
                if (EnchantmentHelper.getLevel(Enchantseries.FIRE_SHOE, boots) > 0) {
                    FireShoe.freezeLava(player, player.getWorld(), player.getBlockPos(), EnchantmentHelper.getLevel(Enchantseries.FIRE_SHOE, boots));
                }
                // 检查靴子是否有特定的附魔 火焰行者
                if (EnchantmentHelper.getLevel(Enchantseries.FIRE_SHOE, boots) > 0) {
                    FireShoe.freezeLava(player, player.getWorld(), player.getBlockPos(), EnchantmentHelper.getLevel(Enchantseries.FIRE_SHOE, boots));
                }
                // 炎霜行者
                if (EnchantmentHelper.getLevel(Enchantseries.FLAME_FROST_WALKER_ENCHANTMENT, boots) > 0) {
                    FlameFrostWalkerEnchantment.freezeLava(player, player.getWorld(), player.getBlockPos(), EnchantmentHelper.getLevel(Enchantseries.FLAME_FROST_WALKER_ENCHANTMENT, boots));
                }
                if (EnchantmentHelper.getLevel(Enchantseries.WATER_WALKER_ENCHANTMENT, boots) > 0) {
                    WaterWalker.walkLand(player, player.getWorld(), player.getBlockPos(), EnchantmentHelper.getLevel(Enchantseries.WATER_WALKER_ENCHANTMENT, boots));
                    if (player.isTouchingWater()) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 10, EnchantmentHelper.getLevel(Enchantseries.WATER_WALKER_ENCHANTMENT, boots) + 1));
                    }
                }
                //枫丹
                if (EnchantmentHelper.getLevel(Enchantseries.FENG_DAN_ENCHANTMENT, boots) > 0 ||
                        EnchantmentHelper.getLevel(Enchantseries.FENG_DAN_ENCHANTMENT, leg) > 0 ||
                        EnchantmentHelper.getLevel(Enchantseries.FENG_DAN_ENCHANTMENT, chest) > 0 ||
                        EnchantmentHelper.getLevel(Enchantseries.FENG_DAN_ENCHANTMENT, head) > 0) {
                    if (player.isTouchingWater() || (player.getWorld().isRaining() && player.getWorld().isSkyVisible(player.getBlockPos()))) {
                        player.damage(FengdanDamage.of(player.getWorld(), FengdanDamage.DAMAGE_TYPE), 1.0F);
                    }
                }
                //天照
                if (EnchantmentHelper.getLevel(Enchantseries.AMATERASU_ENCHANTMENT, head) > 0) {
                    int level = EnchantmentHelper.getLevel(Enchantseries.AMATERASU_ENCHANTMENT, head);
                    BlockHitResult hitResult = (BlockHitResult) player.raycast(40.0D, 0.0F, false);
                    BlockPos blockPos = hitResult.getBlockPos();
                    BlockState blockState = player.getWorld().getBlockState(blockPos);
                    BlockPos firePos = blockPos.up();
                    if (level == 1) {
                        if (!blockState.isAir() && !blockState.equals(Blocks.FIRE.getDefaultState()) && player.getWorld().getBlockState(firePos).isAir()) {
                            player.getWorld().setBlockState(firePos, Blocks.FIRE.getDefaultState());
                        }
                    } else {
                        if (!blockState.isAir() && !blockState.equals(Enchantseries.fire.getDefaultState()) && player.getWorld().getBlockState(firePos).isAir()) {
                            player.getWorld().setBlockState(firePos, Enchantseries.fire.getDefaultState());
                        }
                    }
                }
                //海绵行者
                if (EnchantmentHelper.getLevel(Enchantseries.SPONGE_WALKER_ENCHANTMENT, boots) > 0) {
                    if (player.isTouchingWater()) {
                        SpongeWalkerEnchantment.updateWater(player.getWorld(), player.getBlockPos(), EnchantmentHelper.getLevel(Enchantseries.SPONGE_WALKER_ENCHANTMENT, boots));
                    }
                }
                //熔绵行者
                if (EnchantmentHelper.getLevel(Enchantseries.LAVA_SPONGE_WALKER_ENCHANTMENT, boots) > 0) {
                    if (player.isInLava()) {
                        LavaSpongeWalkerEnchantment.updateLava(player.getWorld(), player.getBlockPos(), EnchantmentHelper.getLevel(Enchantseries.LAVA_SPONGE_WALKER_ENCHANTMENT, boots));
                    }
                }
                //溶解行者
                int dissolveLevel = EnchantmentHelper.getLevel(Enchantseries.DISSOLVE_WALKER_ENCHANTMENT, boots) +
                        EnchantmentHelper.getLevel(Enchantseries.DISSOLVE_WALKER_ENCHANTMENT, leg) +
                        EnchantmentHelper.getLevel(Enchantseries.DISSOLVE_WALKER_ENCHANTMENT, chest) +
                        EnchantmentHelper.getLevel(Enchantseries.DISSOLVE_WALKER_ENCHANTMENT, head);
                if (dissolveLevel > 0) {
                    DissolveWalkerEnchantment.dissolveBlock(player,player.getWorld(), player.getBlockPos(), dissolveLevel);
                }
                //望雷
                if(player.isUsingSpyglass() && (EnchantmentHelper.getLevel(Enchantseries.SPYGLASS_LIGHTNING_ENCHANTMENT,player.getMainHandStack()) > 0 || EnchantmentHelper.getLevel(Enchantseries.SPYGLASS_LIGHTNING_ENCHANTMENT,player.getOffHandStack()) > 0)){
                    SpyglassLightningEnchantment.Instance.lookLightning(player,EnchantmentHelper.getLevel(Enchantseries.SPYGLASS_LIGHTNING_ENCHANTMENT,player.getMainHandStack())+EnchantmentHelper.getLevel(Enchantseries.SPYGLASS_LIGHTNING_ENCHANTMENT,player.getOffHandStack()));
                }
                //远视拆毁
                if(player.isUsingSpyglass() && (EnchantmentHelper.getLevel(Enchantseries.SPYGLASS_DESTRUCT_ENCHANTMENT,player.getMainHandStack()) > 0 || EnchantmentHelper.getLevel(Enchantseries.SPYGLASS_DESTRUCT_ENCHANTMENT,player.getOffHandStack()) > 0 )){
                    SpyglassDestructEnchantment.Instance.lookDestruct(player,EnchantmentHelper.getLevel(Enchantseries.SPYGLASS_DESTRUCT_ENCHANTMENT,player.getMainHandStack()) + EnchantmentHelper.getLevel(Enchantseries.SPYGLASS_DESTRUCT_ENCHANTMENT,player.getOffHandStack()));
                }
                //望破
                if(player.isUsingSpyglass() && (EnchantmentHelper.getLevel(Enchantseries.SPYGLASS_EXPLOSIVE_ENCHANTMENT,player.getMainHandStack()) > 0 || EnchantmentHelper.getLevel(Enchantseries.SPYGLASS_EXPLOSIVE_ENCHANTMENT,player.getOffHandStack()) > 0 )){
                    SpyglassExplosiveEnchantment.Instance.productExplosive(player,EnchantmentHelper.getLevel(Enchantseries.SPYGLASS_EXPLOSIVE_ENCHANTMENT,player.getMainHandStack()) + EnchantmentHelper.getLevel(Enchantseries.SPYGLASS_EXPLOSIVE_ENCHANTMENT,player.getOffHandStack()));
                }
            }
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ExplosiveCoin.onDamaged(player, entity);
            return ActionResult.PASS;
        });
        RambleInterface.EVENT.register((entity) -> {
            if (entity instanceof PlayerEntity player) {
                ItemStack boot = player.getInventory().getArmorStack(0);
                if (EnchantmentHelper.getLevel(Enchantseries.RAMBLE_ENCHANTMENT, boot) > 0) {
                    RambleEnchantment.onJump(entity, EnchantmentHelper.getLevel(Enchantseries.RAMBLE_ENCHANTMENT, boot));
                }
            }
        });
        // 叛逆
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            return true;
        });
        // 复生
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!player.isSpectator()) {
                //随机连锁
                if (EnchantmentHelper.getLevel(Enchantseries.REBEL_ENCHANTMENT, player.getInventory().getMainHandStack()) > 0) {
                    int radius = EnchantmentHelper.getLevel(Enchantseries.REBEL_ENCHANTMENT, player.getInventory().getMainHandStack());
                    Iterator varIterator = BlockPos.iterate(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius)).iterator();
                    List<BlockPos> blocksInRange = new ArrayList<>();
                    while (varIterator.hasNext()) {
                        BlockPos pos1 = (BlockPos) varIterator.next();
                        BlockPos blockPos = new BlockPos(pos1);
                        BlockState blockState = world.getBlockState(blockPos);
                        if (blockState.getBlock() != Blocks.AIR && !pos.equals(pos1) && !blockState.isLiquid()) {
                            blocksInRange.add(blockPos); // 直接添加不可变的 BlockPos
                        }
                    }
                    if (!blocksInRange.isEmpty()) {
                        BlockPos targetPos = blocksInRange.get(random.nextInt(blocksInRange.size()));
                        world.breakBlock(targetPos, true, player);
                    }
                }
                if (EnchantmentHelper.getLevel(Enchantseries.REVIVE_ENCHANTMENT, player.getInventory().getMainHandStack()) > 0) {
                    world.setBlockState(pos, state);
                }
            }
        });
        //注册炎霜行者
        ServerTickEvents.END_SERVER_TICK.register(new TickSchedulerFake());
        //画地为牢
        DrawCircleEnchantment.registerListener();
        //蜘蛛之力
        SpiderPowerEnchantment.registerEvents();
        //打折
        DiscountEnchantment.registerEvents();
        //弹弹行者
        SlimeFeetEnchantment.registerEvents();
        //炎霜行者
        FlameFrostWalkerEnchantment.registerEvents();
        //点石成金
        MidasTouchEnchantment.registerListener();
        //梦想成真
        WishEnchantment.Instance.registerListener();
        //水之女神
        WaterGoddessEnchantment.Instance.registerListener();

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient) {
                //攻击实体 回调繁殖
                int reproductionLevel = EnchantmentHelper.getLevel(Enchantseries.REPRODUCTION_ENCHANTMENT, player.getMainHandStack());
                if (reproductionLevel > 0) {
                    // 检查实体是否是可生成的生物实体
                    if (entity instanceof LivingEntity livingEntity && !(entity instanceof PlayerEntity)) {
                        // 复制实体（生成一个相同类型的实体）
                        for (int i = 0; i < reproductionLevel; i++) {
                            Entity newEntity = livingEntity.getType().create(world);
                            if (newEntity != null) {
                                newEntity.refreshPositionAndAngles(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), livingEntity.getYaw(), livingEntity.getPitch());
                                world.spawnEntity(newEntity);
                            }
                        }
                    }
                }
                //雪刃
                int snowSwordLevel = EnchantmentHelper.getLevel(Enchantseries.SNOW_SWORD_ENCHANTMENT, player.getMainHandStack());
                if(snowSwordLevel > 0){
                    if (entity instanceof LivingEntity livingEntity) {
                        SnowSwordEnchantment.Instance.BlockToPowderSnow(livingEntity, world, entity.getBlockPos(), snowSwordLevel);
                    }
                }
            }
            return ActionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (!player.isSpectator() && !world.isClient) {
                //经验交换
                if(EnchantmentHelper.getLevel(Enchantseries.EXPERIENCE_EXCHANGE_ENCHANTMENT, player.getMainHandStack()) > 0){
                    ExperienceExchangeEnchantment.Instance.cancelDropToExperience(world, player, pos , EnchantmentHelper.getLevel(Enchantseries.EXPERIENCE_EXCHANGE_ENCHANTMENT, player.getMainHandStack()));
                    return false; // 阻止默认的方块破坏行为
                }
            }
            return true;
        });
    }
}
