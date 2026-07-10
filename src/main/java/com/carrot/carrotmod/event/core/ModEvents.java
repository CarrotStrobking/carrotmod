package com.carrot.carrotmod.event.core;

import com.carrot.carrotmod.ability.core.AbilityManager;
import com.carrot.carrotmod.event.player.PlayerUseItemEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;

public class ModEvents {

    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {

                long tick = player.level().getGameTime();

                AbilityManager.tickPlayer(player, tick);
            }
        });

        PlayerUseItemEvent.register();

    }
}