package com.carrot.carrotmod.event.player;

import com.carrot.carrotmod.ability.core.AbilityManager;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

public final class PlayerUseItemEvent {

    public static void register() {

        UseItemCallback.EVENT.register((player, world, hand) -> {

            if (world.isClientSide()) {
                return InteractionResult.PASS;
            }

            AbilityManager.use(
                    (ServerPlayer) player,
                    player.getItemInHand(hand),
                    world.getGameTime()
            );

            return InteractionResult.PASS;
        });
    }
}
