package com.carrot.carrotmod.event;

import com.carrot.carrotmod.item.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class HealingEvents {

    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {

                ItemStack stack = player.getMainHandItem();

                if (!stack.is(ModItems.CARROT_EMPIRE_SWORD))
                    continue;

                if (player.getHealth() >= player.getMaxHealth() / 2)
                    continue;

                if (player.getCooldowns().isOnCooldown(stack))
                    continue;

                player.heal(4.0F);

                player.getCooldowns().addCooldown(stack,20);

            }

        });

    }

}