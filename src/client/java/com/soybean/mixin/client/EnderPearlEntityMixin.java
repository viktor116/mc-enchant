package com.soybean.mixin.client;

import com.soybean.EnchantseriesClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author soybean
 * @date 2024/7/24 11:42
 * @description 闪亮登场 glamorous debut
 */
@Mixin(EnderPearlEntity.class)
public class EnderPearlEntityMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnderPearlEntityMixin.class);

    @Inject(at = @At("HEAD"), method = "onCollision", cancellable = true)
    protected void onCollision(HitResult hitResult, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        NbtCompound nbtCompound = new NbtCompound();
        ProjectileEntity projectileEntity = (ProjectileEntity) (Object) this;
        if (projectileEntity.getOwner() instanceof PlayerEntity player) {
            player.setInvulnerable(true);
            entity.saveNbt(nbtCompound);
            NbtCompound itemTag = nbtCompound.getCompound("Item").getCompound("tag");
            NbtList enchantmentsList = itemTag.getList("Enchantments", NbtElement.COMPOUND_TYPE);
            String id = "";
            int level = 0;
            for (int i = 0; i < enchantmentsList.size(); i++) {
                NbtCompound enchantmentTag = enchantmentsList.getCompound(i);
                id = enchantmentTag.getString("id");// 获取附魔 ID
                level = enchantmentTag.getInt("lvl");// 获取附魔等级
//                LOGGER.info("Enchantment ID: " + id + ", Level: " + level);
            }
            if(level != 0 && id.equals("enchantseries:glamorous_debut")){
                if(level > 1){
                    entity.getWorld().createExplosion(entity, entity.getX(), entity.getY(), entity.getZ(), 5.0f * level,true, World.ExplosionSourceType.BLOCK);
                    BlockPos blockPos = new BlockPos((int)entity.getX(),(int) entity.getY(), (int)entity.getZ());
                    LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(entity.getWorld());
                    if (lightningEntity != null) {
                        lightningEntity.refreshPositionAndAngles(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0.0F, 0.0F);
                        entity.getWorld().spawnEntity(lightningEntity);
                    }
                }else{
                    entity.getWorld().createExplosion(entity, entity.getX(), entity.getY(), entity.getZ(), 5.0f, World.ExplosionSourceType.BLOCK);
                }
                player.setInvulnerable(false);
            }
        }
    }
}
