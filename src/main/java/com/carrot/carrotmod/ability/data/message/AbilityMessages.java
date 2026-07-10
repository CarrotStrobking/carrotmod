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

    public static void shieldActivated(ServerPlayer player) {
        player.displayClientMessage(
                Component.translatable("message.carrotmod.shield_activated"),
                false
        );
    }

    public static void shieldCharging(ServerPlayer player, int percent) {
        player.displayClientMessage(
                Component.translatable(
                        "message.carrotmod.shield_charging",
                        percent
                ),
                true
        );
    }

    public static void shieldLost(ServerPlayer player) {

        player.displayClientMessage(
                Component.translatable(
                        "message.carrotmod.shield_lost"
                ),
                false
        );

    }
    public static void shieldInterrupted(ServerPlayer player){

        player.displayClientMessage(
                Component.translatable(
                        "message.carrotmod.shield_interrupted"
                ),
                false
        );

    }

    public static void shieldCooling(ServerPlayer player) {

        player.sendSystemMessage(
                Component.translatable(
                        "message.carrotmod.shield_cooling"
                ),
                false
        );
    }
}

