package com.carrot.carrotmod.mixin;

import com.carrot.carrotmod.ability.core.AbilityManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ServerPlayer.class)
public abstract class PlayerDamageMixin {

    @Inject(method = "hurtServer", at = @At("TAIL"))
    private void carrotmod$hurtServer(ServerLevel serverLevel,
                                      DamageSource damageSource,
                                      float amount,
                                      CallbackInfoReturnable<Boolean> cir) {

        LivingEntity entity = (LivingEntity)(Object)this;

        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        AbilityManager.damaged(
                player,
                damageSource,
                amount,
                player.level().getGameTime()
        );
    }
}