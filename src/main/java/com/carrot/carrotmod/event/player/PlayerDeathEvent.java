package com.carrot.carrotmod.event.player;

import com.carrot.carrotmod.ability.core.AbilityManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerDeathEvent {

    public static void register() {

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {

            if (!(entity instanceof ServerPlayer player)) {
                return;
            }

            AbilityManager.death(player);

        });
    }
}
