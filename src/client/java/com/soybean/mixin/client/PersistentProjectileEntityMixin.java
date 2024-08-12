package com.soybean.mixin.client;

import com.soybean.EnchantseriesClient;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends Entity {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistentProjectileEntityMixin.class);
    public PersistentProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo info) {
        if (this.getWorld().isClient) {
            return;
        }
        PersistentProjectileEntity projectile = (PersistentProjectileEntity) (Object) this;

        if (projectile.getOwner() instanceof PlayerEntity player) {
            ItemStack bowStack = player.getMainHandStack();
            int level = EnchantmentHelper.getLevel(EnchantseriesClient.FIRE_PATH_ENCHANTMENT, bowStack);
            if (level > 0) {
                Vec3d pos = projectile.getPos();
                BlockPos blockPos = new BlockPos((int)pos.getX(), (int)pos.getY(), (int)pos.getZ());

                while (blockPos.getY() > -256 && this.getWorld().getBlockState(blockPos).isAir()) {
                    blockPos = blockPos.down();
                }
                BlockPos firePos = blockPos.up();
                if (this.getWorld().getBlockState(firePos).isAir() && !this.getWorld().getBlockState(blockPos).equals(EnchantseriesClient.fire.getDefaultState()) &&!this.getWorld().getBlockState(blockPos).equals(Blocks.FIRE.getDefaultState())) {
                    if(level == 1){
                        this.getWorld().setBlockState(firePos, Blocks.FIRE.getDefaultState());
                    }else{
                        this.getWorld().setBlockState(firePos, EnchantseriesClient.fire.getDefaultState());
                    }
                }
            }
        }
    }

    @Inject(method = "onBlockHit", at = @At("HEAD"))
    protected void onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci) {
        PersistentProjectileEntity entity= (PersistentProjectileEntity)(Object) this;
        Entity owner = entity.getOwner();
        if(owner instanceof PlayerEntity player){
            ItemStack mainHandStack = player.getMainHandStack();
            ItemStack offHandStack = player.getOffHandStack();
            BlockPos blockPos = blockHitResult.getBlockPos();
            World world = entity.getWorld();
            //破坏附魔
            if(EnchantmentHelper.getLevel(EnchantseriesClient.DESTRUCT_ENCHANTMENT, mainHandStack) > 0){
//                world.breakBlock(blockPos,true,player);
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    serverPlayer.interactionManager.tryBreakBlock(blockPos);
                }
            }
            Direction side = blockHitResult.getSide();
            BlockPos offsetBlockPos = blockPos.offset(side);
            //建造附魔
            if(EnchantmentHelper.getLevel(EnchantseriesClient.STRUCT_ENCHANTMENT, mainHandStack) > 0){
                if (offHandStack.getItem() instanceof BlockItem) {
                    Block block = ((BlockItem) offHandStack.getItem()).getBlock();
                    world.setBlockState(offsetBlockPos, block.getDefaultState());
                    // 减少副手物品的数量
                    if(!player.isCreative()){
                        offHandStack.decrement(1);
                        if (offHandStack.isEmpty()) {
                            player.getInventory().removeOne(offHandStack);
                        }
                    }
                }
            }
            //爆破附魔
            if(EnchantmentHelper.getLevel(EnchantseriesClient.EXPLOSIVE_ENCHANTMENT, mainHandStack) > 0){
                int level = EnchantmentHelper.getLevel(EnchantseriesClient.EXPLOSIVE_ENCHANTMENT, mainHandStack);
                float explosionPower = 3.0F * level;
//                Explosion explosion = new Explosion(world, null, blockPos.getX()+0.5, blockPos.getY(), blockPos.getZ()+ 0.5, explosionPower, false, Explosion.DestructionType.TRIGGER_BLOCK);
//                explosion.collectBlocksAndDamageEntities();
//                explosion.affectWorld(true);
                this.getWorld().createExplosion(owner, entity.getX()+0.5, entity.getY(), entity.getZ()+0.5, explosionPower,false, World.ExplosionSourceType.BLOCK);
//                    // 生成雷电
//                    LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(this.getWorld());
//                    if (lightningEntity != null) {
//                        lightningEntity.refreshPositionAndAngles(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0.0F, 0.0F);
//                        this.getWorld().spawnEntity(lightningEntity);
//                    }
                this.discard();
            }
        }
    }
}