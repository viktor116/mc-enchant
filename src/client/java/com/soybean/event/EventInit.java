package com.soybean.event;

import com.soybean.Enchantseries;
import com.soybean.EnchantseriesClient;
import com.soybean.Interface.RambleInterface;
import com.soybean.damage.FengdanDamage;
import com.soybean.enchantment.*;
import com.soybean.mixin.client.BowItemMixin;
import com.soybean.networks.ServerNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static net.fabricmc.fabric.api.event.world.WorldTickCallback.*;

/**
 * @author soybean
 * @date 2024/7/19 11:11
 * @description
 */
public class EventInit {

    private static final Logger LOGGER = LoggerFactory.getLogger(Enchantseries.class);

    private static final Random random = new Random();

    public static Map<String,Integer> canShootMap = new HashMap<>();

    public static void start() {
        ServerNetworking.init();
        EVENT.register((listener) -> {
            List<? extends PlayerEntity> players = listener.getPlayers();
            for (PlayerEntity player : players) {
                ItemStack boots = player.getInventory().getArmorStack(0);
                ItemStack leg = player.getInventory().getArmorStack(1);
                ItemStack chest = player.getInventory().getArmorStack(2);
                ItemStack head = player.getInventory().getArmorStack(3);
                // 检查靴子是否有特定的附魔
                if (EnchantmentHelper.getLevel(EnchantseriesClient.FIRE_SHOE, boots) > 0) {
                    FireShoe.freezeLava(player, player.getWorld(), player.getBlockPos(), EnchantmentHelper.getLevel(EnchantseriesClient.FIRE_SHOE, boots));
                }
                if(EnchantmentHelper.getLevel(EnchantseriesClient.WATER_WALKER_ENCHANTMENT,boots)>0){
                    WaterWalker.walkLand(player, player.getWorld(), player.getBlockPos(), EnchantmentHelper.getLevel(EnchantseriesClient.WATER_WALKER_ENCHANTMENT, boots));
                    if(player.isTouchingWater()) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 10 , EnchantmentHelper.getLevel(EnchantseriesClient.WATER_WALKER_ENCHANTMENT, boots) + 1));
                    }
                }
                //枫丹
                if(EnchantmentHelper.getLevel(EnchantseriesClient.FENG_DAN_ENCHANTMENT,boots)>0 ||
                    EnchantmentHelper.getLevel(EnchantseriesClient.FENG_DAN_ENCHANTMENT,leg)>0 ||
                    EnchantmentHelper.getLevel(EnchantseriesClient.FENG_DAN_ENCHANTMENT,chest)>0 ||
                    EnchantmentHelper.getLevel(EnchantseriesClient.FENG_DAN_ENCHANTMENT,head)>0){
                    if(player.isTouchingWater()){
                        player.damage(FengdanDamage.of(player.getWorld(),FengdanDamage.DAMAGE_TYPE), 1.0F);
                    }
                }
                //天照
                if(EnchantmentHelper.getLevel(EnchantseriesClient.AMATERASU_ENCHANTMENT,head) > 0){
                    int level = EnchantmentHelper.getLevel(EnchantseriesClient.AMATERASU_ENCHANTMENT, head);
                    BlockHitResult hitResult = (BlockHitResult) player.raycast(40.0D, 0.0F, false);
                    BlockPos blockPos = hitResult.getBlockPos();
                    BlockState blockState = player.getWorld().getBlockState(blockPos);
                    BlockPos firePos = blockPos.up();
                    if (level == 1) {
                        if(!blockState.isAir() && !blockState.equals(Blocks.FIRE.getDefaultState()) && player.getWorld().getBlockState(firePos).isAir()){
                            player.getWorld().setBlockState(firePos, Blocks.FIRE.getDefaultState());
                        }
                    }else{
                        if(!blockState.isAir() && !blockState.equals(EnchantseriesClient.fire.getDefaultState()) && player.getWorld().getBlockState(firePos).isAir()){
                            player.getWorld().setBlockState(firePos, EnchantseriesClient.fire.getDefaultState());
                        }
                    }
                //连射
//                    if(EnchantmentHelper.getLevel(EnchantseriesClient.CONTINUOUS_FIRE_ENCHANTMENT,player.getMainHandStack())>0 && BowItemMixin.){

//                    }
                }
            }
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ExplosiveCoin.onDamaged(player, entity);
            return ActionResult.PASS;
        });
        RambleInterface.EVENT.register((entity) -> {
            if (entity.getWorld().isClient) {
                if (entity instanceof PlayerEntity player) {
                    ItemStack boot = player.getInventory().getArmorStack(0);
                    if (EnchantmentHelper.getLevel(EnchantseriesClient.RAMBLE_ENCHANTMENT, boot) > 0) {
                        RambleEnchantment.onJump(entity,EnchantmentHelper.getLevel(EnchantseriesClient.RAMBLE_ENCHANTMENT, boot));
                    }
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
                if (EnchantmentHelper.getLevel(EnchantseriesClient.REBEL_ENCHANTMENT, player.getInventory().getMainHandStack()) > 0) {
                    int radius = EnchantmentHelper.getLevel(EnchantseriesClient.REBEL_ENCHANTMENT, player.getInventory().getMainHandStack());
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
                if (EnchantmentHelper.getLevel(EnchantseriesClient.REVIVE_ENCHANTMENT, player.getInventory().getMainHandStack()) > 0) {
                    world.setBlockState(pos, state);
                }
            }
        });

        //画地为牢
        DrawCircleEnchantment.registerListener();
        //蜘蛛之力
        SpiderPowerEnchantment.registerEvents();
        //打折
        DiscountEnchantment.registerEvents();
        //弹弹行者
        SlimeFeetEnchantment.registerEvents();
    }
}
