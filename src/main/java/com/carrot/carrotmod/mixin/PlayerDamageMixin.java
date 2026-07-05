package com.carrot.carrotmod.mixin;

import com.carrot.carrotmod.ability.core.AbilityManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class PlayerDamageMixin {

    @Inject(
            method = "actuallyHurt",
            at = @At("TAIL")
    )
    private void carrotmod$actuallyHurt(ServerLevel level,
                                        DamageSource source,
                                        float amount,
                                        CallbackInfo ci) {

        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof ServerPlayer player))
            return;

        AbilityManager.damaged(
                player,
                source,
                amount,
                level.getServer().getTickCount()
        );
    }
}
