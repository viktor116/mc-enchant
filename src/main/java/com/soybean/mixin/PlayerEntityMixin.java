package com.soybean.mixin;

import com.soybean.Enchantseries;
import com.soybean.Interface.AirSwimInterface;
import com.soybean.utils.CommonUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerEntityMixin.class);
    private boolean canDoubleJump = false;
    private int doubleJumpCooldown = 0;
    private int remainNum = 0;
    private boolean wasOnGroundLastTick = false;
    private boolean isJump = false;

    @Shadow
    public abstract void jump();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    private static final List<BlockState> FLOWERS = Arrays.asList(
            Blocks.DANDELION.getDefaultState(),
            Blocks.POPPY.getDefaultState(),
            Blocks.BLUE_ORCHID.getDefaultState(),
            Blocks.ALLIUM.getDefaultState(),
            Blocks.AZURE_BLUET.getDefaultState(),
            Blocks.RED_TULIP.getDefaultState(),
            Blocks.ORANGE_TULIP.getDefaultState(),
            Blocks.WHITE_TULIP.getDefaultState(),
            Blocks.PINK_TULIP.getDefaultState(),
            Blocks.OXEYE_DAISY.getDefaultState(),
            Blocks.CORNFLOWER.getDefaultState(),
            Blocks.LILY_OF_THE_VALLEY.getDefaultState()
    );

    @Shadow
    protected void onSwimmingStart() {
    }

    @Shadow
    public abstract void tick();

    @Inject(at = @At("HEAD"), method = "travel", cancellable = true)
    public void travel(Vec3d movementInput, CallbackInfo ci) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack armorStack0 = player.getInventory().getArmorStack(0);
        ItemStack armorStack1 = player.getInventory().getArmorStack(1);
        ItemStack armorStack2 = player.getInventory().getArmorStack(2);
        ItemStack armorStack3 = player.getInventory().getArmorStack(3);
        ItemStack mainHandStack = player.getMainHandStack();

        if (EnchantmentHelper.getLevel(Enchantseries.STATIC_ENCHANTMENT, armorStack0) > 0 ||
                EnchantmentHelper.getLevel(Enchantseries.STATIC_ENCHANTMENT, armorStack1) > 0 ||
                EnchantmentHelper.getLevel(Enchantseries.STATIC_ENCHANTMENT, armorStack2) > 0 ||
                EnchantmentHelper.getLevel(Enchantseries.STATIC_ENCHANTMENT, armorStack3) > 0 ||
                EnchantmentHelper.getLevel(Enchantseries.STATIC_ENCHANTMENT, mainHandStack) > 0) {
            ci.cancel();
            return;
        }
        if (EnchantmentHelper.getLevel(Enchantseries.AIR_SWIMMING_ENCHANTMENT, armorStack0) > 0 ||
                EnchantmentHelper.getLevel(Enchantseries.AIR_SWIMMING_ENCHANTMENT, armorStack1) > 0 ||
                EnchantmentHelper.getLevel(Enchantseries.AIR_SWIMMING_ENCHANTMENT, armorStack2) > 0 ||
                EnchantmentHelper.getLevel(Enchantseries.AIR_SWIMMING_ENCHANTMENT, armorStack3) > 0) {
            AirSwimInterface.EVENT.invoker().onSwimming(player);
            if (!player.isSubmergedInWater() && !player.isTouchingWater()) {
                double d = player.getRotationVector().y;
                double e = d < -0.2 ? 0.085 : 0.06;
                if (d <= 0.0) {
                    Vec3d vec3d = player.getVelocity();
                    player.setSwimming(true);
                    player.setVelocity(vec3d.add(0.0, -1 * (d - vec3d.y) * e, 0.0));
                }
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "damage", cancellable = true)
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.isDead())  {
            if(EnchantmentHelper.getLevel(Enchantseries.AIR_SWIMMING_ENCHANTMENT, player.getInventory().getArmorStack(0)) > 0 ||
                    EnchantmentHelper.getLevel(Enchantseries.AIR_SWIMMING_ENCHANTMENT, player.getInventory().getArmorStack(1)) > 0 ||
                    EnchantmentHelper.getLevel(Enchantseries.AIR_SWIMMING_ENCHANTMENT, player.getInventory().getArmorStack(2)) > 0 ||
                    EnchantmentHelper.getLevel(Enchantseries.AIR_SWIMMING_ENCHANTMENT, player.getInventory().getArmorStack(3)) > 0){
                player.getWorld().spawnEntity(new ItemEntity(player.getWorld(), player.getX(), player.getY(), player.getZ(), new ItemStack(Items.TROPICAL_FISH)));
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient) {
            return;
        }
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        //花草行者
        if (EnchantmentHelper.getLevel(Enchantseries.FLOWER_WALKER_ENCHANTMENT, boots) > 0) {
            Vec3d pos = player.getPos();
            BlockPos blockPos = new BlockPos((int) pos.getX(), (int) pos.getY(), (int) pos.getZ());
            BlockState blockState = player.getWorld().getBlockState(blockPos);
            if (blockState.isAir() || blockState.isReplaceable()) {
                BlockState flowerState = FLOWERS.get(CommonUtils.RANDOM.nextInt(FLOWERS.size()));
                player.getWorld().setBlockState(blockPos, flowerState);
            }
        }
        //史莱姆
        if (EnchantmentHelper.getLevel(Enchantseries.SLIME_FEET_ENCHANTMENT, boots) > 0) {
            if (!player.isOnGround() && wasOnGroundLastTick) {
                Vec3d velocity = player.getVelocity();
                player.setVelocity(velocity.x, 0.9, velocity.z); // Bounce effect
                //todo 验证测试史莱姆
            }
        }
        //连跳
        int double_jump_level = EnchantmentHelper.getLevel(Enchantseries.DOUBLE_JUMP_ENCHANTMENT, boots);
        if (double_jump_level > 0) {
            handleDoubleJump(player, double_jump_level, this.isJump);
        }

    }

    @Inject(method = "jump", at = @At("TAIL"))
    public void jump(CallbackInfo info) {
        this.isJump = true;
        PlayerEntity player = (PlayerEntity) (Object) this;
        //爆破行者
        World world = player.getWorld();
        if(!world.isClient){
            List<ItemStack> playerInventory = CommonUtils.getPlayerInventory(player);
            int level = EnchantmentHelper.getLevel(Enchantseries.EXPLOSIVE_WALKER_ENCHANTMENT, playerInventory.get(CommonUtils.FEET));
            if(level > 0){
                // 获取玩家当前的位置
                BlockPos blockPos = player.getBlockPos();
                float explosionPower = 1.0F + level;

                // 临时设置玩家为无敌状态
                player.setInvulnerable(true);
                this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), explosionPower,false, World.ExplosionSourceType.BLOCK);
//                Explosion explosion = new Explosion(world, null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), explosionPower, false, Explosion.DestructionType.DESTROY);
//
//                explosion.collectBlocksAndDamageEntities();
//                explosion.affectWorld(true);
                // 推动玩家
                float pushStrength = 1.0F + (level * 0.1F); // 推动玩家的力度，可以调整
                double motionX = -Math.sin(player.getYaw() * (Math.PI / 180.0)) * pushStrength * 2;
                double motionZ = Math.cos(player.getYaw() * (Math.PI / 180.0)) * pushStrength * 2;
                player.addVelocity(motionX, level * 0.3F, motionZ);
                player.velocityModified = true;
                MinecraftServer server = world.getServer();
                if (server != null) {
                    server.execute(() -> player.setInvulnerable(false));
                }
            }
        }
    }

    @Unique
    private void handleDoubleJump(PlayerEntity player, Integer level, boolean jump) {
        if (player.isOnGround()) {
            canDoubleJump = true;
            doubleJumpCooldown = 0;
            remainNum = level;
        } else {
            if (doubleJumpCooldown > 0) {
                doubleJumpCooldown--;
            }
//            LOGGER.info("canDoubleJump={}, doubleJumpCooldown={}, jump={}", canDoubleJump, doubleJumpCooldown, jump);
            if (canDoubleJump && doubleJumpCooldown == 0 && jump) {
                Vec3d velocity = player.getVelocity();
                if (velocity.y < 0.2) { // Player is falling and not immediately after a jump
                    player.setVelocity(velocity.x, 0.5, velocity.z);
                    player.velocityModified = true;

                    // Play a sound effect for the double jump
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS,
                            1.0F, 1.0F);
                    // Spawn some particles for visual effect
                    spawnDoubleJumpParticles(player);
                    remainNum--;
                    if (remainNum == 0) {
                        canDoubleJump = false;
                        doubleJumpCooldown = 20; // 1 second cooldown (20 ticks)
                        this.isJump = false;
                    }
                }
            }
        }
    }

    @Unique
    private void spawnDoubleJumpParticles(PlayerEntity player) {
        for (int i = 0; i < 20; i++) {
            double d0 = player.getRandom().nextGaussian() * 0.02D;
            double d1 = player.getRandom().nextGaussian() * 0.02D;
            double d2 = player.getRandom().nextGaussian() * 0.02D;
            ((ServerWorld) player.getWorld()).spawnParticles(ParticleTypes.CLOUD,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    1, d0, d1, d2, 0.1);
        }
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    public void handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);

        if (EnchantmentHelper.getLevel(Enchantseries.SLIME_FEET_ENCHANTMENT, boots) > 0) {
            // Prevent fall damage and reset fall distance
            player.fallDistance = 0;
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}