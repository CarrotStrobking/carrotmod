package com.carrot.carrotmod.mixin;

import com.carrot.carrotmod.ability.core.AbilityManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public abstract class PlayerDamageModifyMixin {

    @ModifyVariable(
            method = "actuallyHurt",
            at = @At("HEAD"),
            ordinal = 0
    )
    private float carrotmod$modifyDamage(
            float damage,
            ServerLevel level,
            DamageSource source
    ) {

        Player player = (Player)(Object)this;

        return AbilityManager.modifyDamageTaken(
                player,
                source,
                damage
        );
    }
}
