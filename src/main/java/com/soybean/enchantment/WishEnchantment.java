package com.soybean.enchantment;

import com.soybean.Enchantseries;
import com.soybean.utils.CommonUtils;
import com.soybean.utils.SendMessageToClientUtils;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Iterator;

/**
 * @author soybean
 * @date 2024/9/26 11:11
 * @description
 */
public class WishEnchantment extends Enchantment{
    public static WishEnchantment Instance;
    private static final Logger LOGGER = LoggerFactory.getLogger(WaterGoddessEnchantment.class);

    public WishEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.VANISHABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        Instance = this;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    public int getMaxLevel() {
        return 1;
    }

    public void registerListener() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient) return TypedActionResult.pass(ItemStack.EMPTY);
            ItemStack stack = player.getStackInHand(hand);
            int wishLevel = EnchantmentHelper.getLevel(Enchantseries.WISH_ENCHANTMENT, stack);
            if (wishLevel > 0) {
                NbtCompound nbtCompound = stack.getNbt();
                if (nbtCompound == null) {
                    return TypedActionResult.pass(stack);
                }
                NbtList pagesNbtList = nbtCompound.getList("pages", NbtElement.STRING_TYPE);
                for (int i = 0; i < pagesNbtList.size(); i++) {
                    String content = pagesNbtList.getString(i).replace("\"","");
                    handleSpawnLogic(player, content);
                }
                return TypedActionResult.fail(ItemStack.EMPTY);
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });
    }

    //处理生成逻辑
    private void handleSpawnLogic(PlayerEntity player, String content){
        ServerWorld serverWorld = (ServerWorld) player.getWorld();
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        Item spawnItem = null; //需要生成的物品
        if(content.startsWith("我想要")){
            if(content.contains("只") || content.contains("个")){ //动物
                EntityType entityType = null;
                String messageEntity = null;
                if(content.contains("羊")){
                    entityType = EntityType.SHEEP;
                    messageEntity = "羊";
                }else if(content.contains("牛")){
                    entityType = EntityType.COW;
                    messageEntity = "牛";
                }else if(content.contains("猪")){
                    entityType = EntityType.PIG;
                    messageEntity = "猪";
                }else if(content.contains("鸡")){
                    entityType = EntityType.CHICKEN;
                    messageEntity = "鸡";
                }else if(content.contains("兔子")){
                    entityType = EntityType.RABBIT;
                    messageEntity = "兔子";
                }else if(content.contains("马")){
                    entityType = EntityType.HORSE;
                    messageEntity = "马";
                }else if(content.contains("骡")){
                    entityType = EntityType.DONKEY;
                    messageEntity = "骡";
                }else if(content.contains("驴")){
                    entityType = EntityType.MULE;
                    messageEntity = "驴";
                }else if(content.contains("羊驼")){
                    entityType = EntityType.LLAMA;
                    messageEntity = "羊驼";
                }else if(content.contains("绵羊")){
                    entityType = EntityType.SHEEP;
                    messageEntity = "绵羊";
                }else if(content.contains("僵尸")){
                    entityType = EntityType.ZOMBIE;
                    messageEntity = "僵尸";
                }
                if(entityType != null){
                    if(content.contains("*")){
                        String[] split = content.split("\\*");
                        boolean isNumeric = split[1].matches("\\d+");
                        if(isNumeric){
                            int num = Integer.parseInt(split[1]);
                            for (int i = 0; i < Math.min(25565,num); i++) {
                                spawnLivingEntity(player, entityType);
                                SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"愿望实现:"+messageEntity+"*"+num);
                            }
                        }else{
                            spawnLivingEntity(player, entityType);
                            SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"愿望实现:"+messageEntity);
                        }
                    }else{
                        spawnLivingEntity(player, entityType);
                        SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"愿望实现:"+messageEntity);
                    }
                }
            } else if (content.contains("死")) { //死亡
                player.kill();
                SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"愿望实现:送你去死");
            } else if(content.contains("下雨")){
                serverWorld.setWeather(0,10000,true,false);
                SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"愿望实现:现在开始下雨");
            } else if(content.contains("天晴")){
                serverWorld.setWeather(0,0,false,false);
                SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"愿望实现:现在开始放晴");
            } else if(content.contains("变成晚上")){
                player.getServer().getWorld(player.getWorld().getRegistryKey()).setTimeOfDay(18000); // 将世界时间重置到0，即一天的开始
                SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"愿望实现:变成晚上");
            } else if(content.contains("变成白天")){
                player.getServer().getWorld(player.getWorld().getRegistryKey()).setTimeOfDay(6000); // 将世界时间重置到0，即一天的开始
                SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"愿望实现:变成白天");
            } else if(content.contains("四周变成平地")){
                handleGroundChange(player);
                SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"愿望实现:四周正在变成平地");
            } else if(content.contains("四周变成钻石块")){
                handleChangeBlock(player, Blocks.DIAMOND_BLOCK.getDefaultState());
                SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"愿望实现:四周正在变成钻石块");
            } else if(content.contains("四周变成冰块")){
                handleChangeBlock(player, Blocks.ICE.getDefaultState());
                SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"愿望实现:四周正在变成冰块");
            }else { //生成物品
                String messageItem = null;
                if(content.contains("面包")){
                    spawnItem = Items.BREAD;
                    messageItem = "面包";
                }
                if(content.contains("圆石")){
                    spawnItem = Items.COBBLESTONE;
                    messageItem = "圆石";
                }
                if(content.contains("钻石")){
                    spawnItem = Items.DIAMOND;
                    messageItem = "钻石";
                }
                if(content.contains("钻石剑")){
                    spawnItem = Items.DIAMOND_SWORD;
                    messageItem = "钻石剑";
                }
                if(content.contains("附魔金苹果")){
                    spawnItem = Items.ENCHANTED_GOLDEN_APPLE;
                    messageItem = "附魔金苹果";
                }
                if(spawnItem != null){
                    if(content.contains("*")){
                        String[] split = content.split("\\*");
                        boolean isNumeric = split[1].matches("\\d+");
                        if(isNumeric){
                            int num = Integer.parseInt(split[1]);
                            for (int i = 0; i < Math.min(25565,num); i++) {
                                spawnItemEntity(player, spawnItem);
                                SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"愿望实现:"+messageItem+"*"+num);
                            }
                        }else{
                            spawnItemEntity(player, spawnItem);
                            SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"愿望实现:"+messageItem);
                        }
                    }else{
                        spawnItemEntity(player, spawnItem);
                        SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"愿望实现:"+messageItem);
                    }
                }else{
                    SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"这个愿望暂时无法实现");
                }
            }
        }else{
            SendMessageToClientUtils.sendOverlayMessage(serverPlayer,"这个愿望暂时无法实现");
            // 处理错误逻辑
            // MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.of("神力-磁吸觉醒"), false);
        }
    }

    //处理生成物品
    private void spawnItemEntity(PlayerEntity player, Item item) {
        float radius = 5.0f;
        double offsetX = Math.cos(CommonUtils.RANDOM.nextDouble() * Math.PI * 2) * radius;
        double offsetZ = Math.sin(CommonUtils.RANDOM.nextDouble() * Math.PI * 2) * radius;
        ItemEntity itemEntity = new ItemEntity(player.getWorld(), player.getX(), player.getY(), player.getZ(), new ItemStack(item));

        itemEntity.setPos(player.getX() + offsetX, player.getY() + player.getStandingEyeHeight() +  10.0D, player.getZ() + offsetZ);
        itemEntity.setVelocity(new Vec3d(0, 0.2, 0));
        itemEntity.setPickupDelay(40); // 设置延迟为600 ticks
        player.getWorld().spawnEntity(itemEntity);
    }

    //处理生成生物
    private void spawnLivingEntity(PlayerEntity player, EntityType<? extends MobEntity> entityType) {
        MobEntity mobEntity = entityType.create(player.getWorld());
        if (mobEntity == null) {
            LOGGER.error("Failed to create entity of type: " + entityType.toString());
            return;
        }
        float radius = 5.0f;
        double offsetX = Math.cos(CommonUtils.RANDOM.nextDouble() * Math.PI * 2) * radius;
        double offsetZ = Math.sin(CommonUtils.RANDOM.nextDouble() * Math.PI * 2) * radius;
        mobEntity.refreshPositionAndAngles(player.getX() + offsetX, player.getY() + player.getStandingEyeHeight() +  3.0D, player.getZ() + offsetZ, 0, 0.0F);
        mobEntity.setVelocity(Vec3d.ZERO);

        if (player.getWorld().spawnEntity(mobEntity)) {
            mobEntity.playSpawnEffects();
            mobEntity.setVelocity(0, 0.2, 0);  // 给实体一个向上的初始速度
        } else {
            LOGGER.error("Failed to spawn entity in the world");
        }
    }

    //处理四周变平地
    private void handleGroundChange(PlayerEntity player) {
        BlockState blockState = Blocks.AIR.getDefaultState();
        BlockPos blockPos = player.getBlockPos();
        World world = player.getWorld();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Iterator var7;
        var7 = BlockPos.iterate(blockPos.add(-32, 0, -32), blockPos.add(32, 30, 32)).iterator();
        while (var7.hasNext()) {
            BlockPos blockPos2 = (BlockPos) var7.next();
            mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
            BlockState blockState3 = world.getBlockState(blockPos2);
            if (blockState3 != Blocks.AIR.getDefaultState() && blockState.canPlaceAt(world, blockPos2) && world.canPlace(blockState, blockPos2, ShapeContext.absent())) {
                world.setBlockState(blockPos2, blockState);
                world.playSound((PlayerEntity)null, blockPos2, SoundEvents.BLOCK_CROP_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }
    //处理变其他方块
    private void handleChangeBlock(PlayerEntity player,BlockState blockState) {
        BlockPos blockPos = player.getBlockPos();
        World world = player.getWorld();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Iterator var7;
        var7 = BlockPos.iterate(blockPos.add(-32, -8, -32), blockPos.add(32, 16, 32)).iterator();
        while (var7.hasNext()) {
            BlockPos blockPos2 = (BlockPos) var7.next();
            mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
            BlockState blockState3 = world.getBlockState(blockPos2);
            if (blockState3 != Blocks.DIAMOND_BLOCK.getDefaultState() && blockState3 != Blocks.AIR.getDefaultState() && blockState.canPlaceAt(world, blockPos2) && world.canPlace(blockState, blockPos2, ShapeContext.absent())) {
                world.setBlockState(blockPos2, blockState);
                world.playSound((PlayerEntity)null, blockPos2, SoundEvents.BLOCK_CROP_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

}
