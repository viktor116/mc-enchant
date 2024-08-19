package com.soybean.mixin.client;

import com.soybean.EnchantseriesClient;
import com.soybean.utils.CommonUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;

/**
 * @author soybean
 * @date 2024/7/27 14:03
 * @description
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected boolean jumping;

    @Shadow public abstract void enterCombat();

    private static final Logger LOGGER = LoggerFactory.getLogger(LivingEntityMixin.class);

    @Inject(at = @At("RETURN"), method = "damage", cancellable = true)
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getAttacker();
        Object o = this;
        PlayerEntity player = null;
        if(o instanceof PlayerEntity){
            player = (PlayerEntity) o;
        }
        if(player != null){ //化敌为友
            List<ItemStack> playerInventory = CommonUtils.getPlayerInventory(player);
            if(attacker instanceof MobEntity mob){
                if (EnchantmentHelper.getLevel(EnchantseriesClient.TO_FRIEND_ENCHANTMENT, playerInventory.get(CommonUtils.FEET)) > 0
                        || EnchantmentHelper.getLevel(EnchantseriesClient.TO_FRIEND_ENCHANTMENT, playerInventory.get(CommonUtils.HEAD)) > 0
                        || EnchantmentHelper.getLevel(EnchantseriesClient.TO_FRIEND_ENCHANTMENT, playerInventory.get(CommonUtils.CHEST)) > 0
                        || EnchantmentHelper.getLevel(EnchantseriesClient.TO_FRIEND_ENCHANTMENT, playerInventory.get(CommonUtils.LEGS)) > 0){
                    if (attacker.isAttackable() && attacker.isLiving()) {
                        List<LivingEntity> nearbyHostiles = attacker.getWorld().getNonSpectatingEntities(LivingEntity.class,
                                attacker.getBoundingBox().expand(7, 5, 7));
                        for (LivingEntity target : nearbyHostiles) {
                            if (target != attacker && target != player) {
                                mob.setTarget(target); // 使用已经转换好的 mobUser
                                mob.setAttacker(target);
                                break;
                            }
                        }
                    }
                }
            }
        }
        LivingEntity entity = (LivingEntity) (Object) this;
        int enchantmentLevel = EnchantmentHelper.getEquipmentLevel(EnchantseriesClient.FIRE_RECOVER_ENCHANTMENT, entity);

        if (enchantmentLevel > 0 && (source.isIn(DamageTypeTags.IS_FIRE) || source.isOf(DamageTypes.ON_FIRE))) {
            EnchantseriesClient.FIRE_RECOVER_ENCHANTMENT.onUserDamaged(entity, source.getAttacker(), enchantmentLevel);
            cir.setReturnValue(false); // Cancel the damage
        }
    }
    @Inject(at = @At("HEAD"), method = "damage", cancellable = true)
    public void damage2(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // 检查实体是否为玩家，以及伤害来源是否为岩浆块
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof PlayerEntity p) {
            BlockPos pos = p.getBlockPos().down();
            Block block = p.getWorld().getBlockState(pos).getBlock();
            // 检查玩家是否站在岩浆块上，以及是否拥有FlameFrostWalker附魔
//            if (block == Blocks.MAGMA_BLOCK && hasFlameFrostWalkerEnchantment(p)) {
                if (source.isOf(DamageTypes.HOT_FLOOR) && hasFlameFrostWalkerEnchantment(p)) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
//            }
        }
    }

    @Inject(at = @At("HEAD"), method = "travel", cancellable = true)
    public void travel(Vec3d movementInput, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        World world = entity.getWorld();
        if(!world.isClient){
            int channelLevel = EnchantmentHelper.getLevel(EnchantseriesClient.CHANNELING_REVISE_ENCHANTMENT, entity.getEquippedStack(EquipmentSlot.FEET));
            LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(entity.getWorld());
            if(channelLevel == 1) {
                if (lightningEntity != null) {
                    lightningEntity.refreshPositionAndAngles(entity.getPos().getX() + 0.5, entity.getPos().getY(), entity.getPos().getZ() + 0.5, 0.0F, 0.0F);
                    entity.getWorld().spawnEntity(lightningEntity);
                }
            }else if(channelLevel > 1){
                // 限制每次只生成一个闪电，并且只对最靠近的敌对生物生效
                List<LivingEntity> nearbyHostiles = entity.getWorld().getNonSpectatingEntities(LivingEntity.class, entity.getBoundingBox().expand(7, 3, 7));
                nearbyHostiles.removeIf(target -> target == entity); // 移除自己
                nearbyHostiles.sort(Comparator.comparingDouble(target -> target.squaredDistanceTo(entity))); // 按距离排序
                if (!nearbyHostiles.isEmpty()) {
                    LivingEntity closestTarget = nearbyHostiles.get(0); // 获取最近的敌对生物
                    if (lightningEntity != null) {
                        lightningEntity.refreshPositionAndAngles(closestTarget.getPos().getX() + 0.5, closestTarget.getPos().getY(), closestTarget.getPos().getZ() + 0.5, 0.0F, 0.0F);
                        closestTarget.getWorld().spawnEntity(lightningEntity);
                    }
                }
            }
        }
    }

    private boolean hasFlameFrostWalkerEnchantment(PlayerEntity player) {
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        return EnchantmentHelper.getLevel(EnchantseriesClient.FLAME_FROST_WALKER_ENCHANTMENT, boots) > 0;
    }
}
