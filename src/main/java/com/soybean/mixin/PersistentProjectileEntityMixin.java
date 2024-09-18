package com.soybean.mixin;

import com.soybean.Enchantseries;
import com.soybean.enchantment.ArrowsRainEnchantment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends Entity {

    @Shadow private ItemStack stack;
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistentProjectileEntityMixin.class);

    private Integer arrowsRainNum = 0;

    private boolean isFireArrowRain = false;
    private HitResult saveHitResult;
    private boolean hasTriggeredFreeze = false;
    public PersistentProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo info) {
        if (this.getWorld().isClient) {
            return;
        }
        PersistentProjectileEntity projectile = (PersistentProjectileEntity) (Object) this;
        World world = projectile.getWorld();

        if (projectile.getOwner() instanceof PlayerEntity player) {
            ItemStack bowStack = player.getMainHandStack();
            if (!player.getWorld().isClient && projectile instanceof ArrowEntity arrowEntity) {
                //火焰之径
                if(EnchantmentHelper.getLevel(Enchantseries.FIRE_PATH_ENCHANTMENT, bowStack) > 0){
                    int level = EnchantmentHelper.getLevel(Enchantseries.FIRE_PATH_ENCHANTMENT, bowStack);
                    Vec3d pos = projectile.getPos();
                    BlockPos blockPos = new BlockPos((int)pos.getX(), (int)pos.getY(), (int)pos.getZ());
                    while (blockPos.getY() > -256 && this.getWorld().getBlockState(blockPos).isAir()) {
                        blockPos = blockPos.down();
                    }
                    BlockPos firePos = blockPos.up();
                    if (this.getWorld().getBlockState(firePos).isAir() && !this.getWorld().getBlockState(blockPos).equals(Enchantseries.fire.getDefaultState()) &&!this.getWorld().getBlockState(blockPos).equals(Blocks.FIRE.getDefaultState())) {
                        if(level == 1){
                            this.getWorld().setBlockState(firePos, Blocks.FIRE.getDefaultState());
                        }else{
                            this.getWorld().setBlockState(firePos, Enchantseries.fire.getDefaultState());
                        }
                    }
                }
                //箭雨
                if(EnchantmentHelper.getLevel(Enchantseries.ARROWS_RAIN_ENCHANTMENT, bowStack) > 0){
                    if(isFireArrowRain){
                        Integer maxNum = EnchantmentHelper.getLevel(Enchantseries.ARROWS_RAIN_ENCHANTMENT, bowStack)+7;
                        if(ArrowsRainEnchantment.isPlayerArrow) ArrowsRainEnchantment.isPlayerArrow = false;
                        if(arrowsRainNum < maxNum){
                            ArrowsRainEnchantment.shootArrowRain(saveHitResult,EnchantmentHelper.getLevel(Enchantseries.ARROWS_RAIN_ENCHANTMENT, bowStack),player,arrowEntity);
                            arrowsRainNum ++;
                        }else{
                            arrowsRainNum = 0;
                            isFireArrowRain = false;
                        }
                    }
                }
                //冻结
                if (EnchantmentHelper.getLevel(Enchantseries.ARROW_FREEZE_ENCHANTMENT, bowStack) > 0) {
                    if (projectile.isSubmergedInWater() && !hasTriggeredFreeze) {
                        freezeWaterAroundArrow(projectile, world, bowStack);
                        hasTriggeredFreeze = true;
                    }
                }
            }
        }
    }

    @Inject(method = "onBlockHit", at = @At("HEAD"))
    protected void onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci) {
        PersistentProjectileEntity entity= (PersistentProjectileEntity)(Object) this;
        Entity owner = entity.getOwner();
        if(owner instanceof LivingEntity player){
            ItemStack mainHandStack = player.getMainHandStack();
            ItemStack offHandStack = player.getOffHandStack();
            BlockPos blockPos = blockHitResult.getBlockPos();
            World world = entity.getWorld();
            if(!world.isClient){
                //箭雨
                int arrowRainLevel = EnchantmentHelper.getLevel(Enchantseries.ARROWS_RAIN_ENCHANTMENT, mainHandStack);
                if(arrowRainLevel > 0){
                    saveHitResult = blockHitResult;
                    if(!isFireArrowRain && ArrowsRainEnchantment.isPlayerArrow){
                        isFireArrowRain = true;
                        return;
                    }
                }
                //破坏附魔
                if(EnchantmentHelper.getLevel(Enchantseries.DESTRUCT_ENCHANTMENT, mainHandStack) > 0){
//                world.breakBlock(blockPos,true,player);
                    if (player instanceof ServerPlayerEntity serverPlayer) {
                        serverPlayer.interactionManager.tryBreakBlock(blockPos);
                    }
                    this.discard();
                }
                Direction side = blockHitResult.getSide();
                BlockPos offsetBlockPos = blockPos.offset(side);
                //建造附魔
                if(EnchantmentHelper.getLevel(Enchantseries.STRUCT_ENCHANTMENT, mainHandStack) > 0){
                    if (offHandStack.getItem() instanceof BlockItem) {
                        Block block = ((BlockItem) offHandStack.getItem()).getBlock();
                        world.setBlockState(offsetBlockPos, block.getDefaultState());
                        // 减少副手物品的数量
                        if (player instanceof PlayerEntity playerEntity) {
                            offHandStack.decrement(1);
                            if (offHandStack.isEmpty()) {
                                playerEntity.getInventory().removeOne(offHandStack);
                            }
                        }
                    }
                    if(arrowRainLevel <= 0)  this.discard();
                    this.discard();
                }
                //爆破附魔
                if(EnchantmentHelper.getLevel(Enchantseries.EXPLOSIVE_ENCHANTMENT, mainHandStack) > 0){
                    int level = EnchantmentHelper.getLevel(Enchantseries.EXPLOSIVE_ENCHANTMENT, mainHandStack);
                    float explosionPower = 3.0F * level;
                    this.getWorld().createExplosion(owner, entity.getX()+0.5, entity.getY(), entity.getZ()+0.5, explosionPower,false, World.ExplosionSourceType.BLOCK);
                    if(arrowRainLevel <= 0)  this.discard();
                    this.discard();
                }
                //烈焰之地
                if(EnchantmentHelper.getLevel(Enchantseries.FLAME_PLACE_ENCHANTMENT, mainHandStack)>0){
                    BlockState blockState = Blocks.FIRE.getDefaultState();
                    int level = EnchantmentHelper.getLevel(Enchantseries.FLAME_PLACE_ENCHANTMENT, mainHandStack);
                    int i = Math.min(18, 2 + level*2);
                    BlockPos.Mutable mutable = new BlockPos.Mutable();
                    Iterator var7 = BlockPos.iterate(blockPos.add(-i, 1, -i), blockPos.add(i, 1, i)).iterator();
                    while (var7.hasNext()) {
                        BlockPos blockPos2 = (BlockPos) var7.next();
                        if (blockPos2.isWithinDistance(entity.getPos(), (double) i)) {
                            mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
                            BlockState blockState2 = world.getBlockState(mutable);
                            if (blockState2.isAir()) {
                                BlockState blockState3 = world.getBlockState(blockPos2);
                                if (blockState3 == Blocks.AIR.getDefaultState() && blockState.canPlaceAt(world, blockPos2) && world.canPlace(blockState, blockPos2, ShapeContext.absent())) {
                                    world.setBlockState(blockPos2, blockState);
                                }
                            }
                        }
                    }
                    ((ServerWorld)world).spawnParticles(ParticleTypes.FLAME,
                            entity.getX(), entity.getY(), entity.getZ(),
                            50,
                            i, i, i,
                            0.1
                    );
                    this.discard();
                }
            }
        }
    }

    @Inject(method = "onEntityHit", at = @At("HEAD"))
    protected void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        PersistentProjectileEntity entity= (PersistentProjectileEntity)(Object) this;
        Entity owner = entity.getOwner();
        if(owner instanceof LivingEntity player){
            ItemStack mainHandStack = player.getMainHandStack();
            if(!player.getWorld().isClient){
                //爆破附魔
                if(EnchantmentHelper.getLevel(Enchantseries.EXPLOSIVE_ENCHANTMENT, mainHandStack) > 0){
                    int level = EnchantmentHelper.getLevel(Enchantseries.EXPLOSIVE_ENCHANTMENT, mainHandStack);
                    float explosionPower = 3.0F * level;
                    this.getWorld().createExplosion(owner, entity.getX()+0.5, entity.getY(), entity.getZ()+0.5, explosionPower,false, World.ExplosionSourceType.BLOCK);
                }
                //箭雨
                if(EnchantmentHelper.getLevel(Enchantseries.ARROWS_RAIN_ENCHANTMENT, mainHandStack) > 0){
                    saveHitResult = entityHitResult;
                    isFireArrowRain = true;
                }
            }
        }
    }

    private void freezeWaterAroundArrow(PersistentProjectileEntity projectile, World world, ItemStack bowStack) {
        BlockPos arrowPos = projectile.getBlockPos();
        int enchantmentLevel = EnchantmentHelper.getLevel(Enchantseries.ARROW_FREEZE_ENCHANTMENT, bowStack);
        int freezeRadius = 2 + enchantmentLevel;  // Adjust base radius as needed

        BlockState iceState = Blocks.ICE.getDefaultState();

        for (BlockPos pos : BlockPos.iterate(
                arrowPos.add(-freezeRadius, -freezeRadius, -freezeRadius),
                arrowPos.add(freezeRadius, freezeRadius, freezeRadius))) {
            if (pos.isWithinDistance(arrowPos, freezeRadius)) {
                BlockState state = world.getBlockState(pos);
                if (state.getFluidState().isOf(Fluids.WATER)) {
                    world.setBlockState(pos, iceState);
                }
            }
        }

        ((ServerWorld)world).spawnParticles(ParticleTypes.SNOWFLAKE,
                arrowPos.getX(), arrowPos.getY(), arrowPos.getZ(),
                50,
                freezeRadius, freezeRadius, freezeRadius,
                0.1
        );
        this.discard();
    }
}