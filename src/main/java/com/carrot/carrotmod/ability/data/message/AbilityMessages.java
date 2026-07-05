package com.carrot.carrotmod.ability.data.message;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class AbilityMessages {

    private AbilityMessages() {
    }

    public static void holyPowerDepleted(ServerPlayer player) {
        player.displayClientMessage(
                Component.translatable("message.carrotmod.holy_power_depleted"),
                false
        );
    }

    public static void holyPowerRecovering(ServerPlayer player) {
        player.displayClientMessage(
                Component.translatable("message.carrotmod.holy_power_recovering"),
                false
        );
    }

    public static void holyPowerRestored(ServerPlayer player) {
        player.displayClientMessage(
                Component.translatable("message.carrotmod.holy_power_restored"),
                false
        );
    }

    public static void holyPowerBar(ServerPlayer player, String bar, int power, int maxPower) {
        player.displayClientMessage(
                Component.translatable(
                        "message.carrotmod.holy_power_bar",
                        bar,
                        power,
                        maxPower
                ),
                true
        );
    }
}

