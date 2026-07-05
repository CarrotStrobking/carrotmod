package com.carrot.carrotmod.ability.weapon.carrot;

import com.carrot.carrotmod.ability.core.Ability;
import com.carrot.carrotmod.ability.data.ItemAbilityData;
import com.carrot.carrotmod.ability.data.message.AbilityMessages;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;

public class CarrotSwordAbility implements Ability {

    private static final int MAX_POWER = 5;
    private static final int RECOVER_INTERVAL = 160;
    private static final int HEAL_COOLDOWN = 20;
    private static final float HEAL_AMOUNT = 8.0F;
    private static final int CHANNEL_TIME = 60;
    private static final int SHIELD_DURATION = 800;
    private static final int SHIELD_COOLDOWN = 1200;

    @Override
    public void tick(ServerPlayer player, ItemStack stack, long tick) {

        if (channel(player, stack, tick)) {
            return;
        }

        heal(player, stack, tick);

        recover(player, stack, tick);

        show(player, stack);
    }


    @Override
    public void damaged(ServerPlayer player,
                        ItemStack stack,
                        DamageSource source,
                        float amount,
                        long tick) {

        ItemAbilityData.setLastDamageTick(stack, tick);
    }


    private void heal(ServerPlayer player, ItemStack stack, long tick) {

        // 血量高于50%，不回血
        if (player.getHealth() >= player.getMaxHealth() / 2.0F) {
            return;
        }

        int power = ItemAbilityData.getHolyPower(stack);

        // 没充能
        if (power <= 0) {
            return;
        }

        long nextHeal = ItemAbilityData.getNextHealTick(stack);

        // 冷却中
        if (tick < nextHeal) {
            return;
        }

        // 回血
        player.heal(HEAL_AMOUNT);

        // 消耗一层
        int newPower = power - 1;

        ItemAbilityData.setHolyPower(stack, newPower);


        // 设置20tick冷却
        ItemAbilityData.setNextHealTick(stack, tick + HEAL_COOLDOWN);

        if (newPower == 0) {

            AbilityMessages.holyPowerDepleted(player);

            ItemAbilityData.setWasEmpty(stack, true);
            ItemAbilityData.setWasFull(stack, false);
        }


    }

    private void recover(ServerPlayer player, ItemStack stack, long tick) {

        int power = ItemAbilityData.getHolyPower(stack);

        if (power >= MAX_POWER) {
            return;
        }

        long lastDamage = ItemAbilityData.getLastDamageTick(stack);

        if (tick - lastDamage < RECOVER_INTERVAL) {
            return;
        }

        long lastRecover = ItemAbilityData.getLastRecoverTick(stack);

        if (tick - lastRecover < RECOVER_INTERVAL) {
            return;
        }

        power++;

        ItemAbilityData.setHolyPower(stack, power);
        ItemAbilityData.setLastRecoverTick(stack, tick);

        if (power == 1 && ItemAbilityData.wasEmpty(stack)) {

            AbilityMessages.holyPowerRecovering(player);

            ItemAbilityData.setWasEmpty(stack, false);
        }

        if (power < MAX_POWER) {
            ItemAbilityData.setWasFull(stack, false);
        }

        if (power == MAX_POWER && !ItemAbilityData.wasFull(stack)) {

            AbilityMessages.holyPowerRestored(player);
            ItemAbilityData.setWasFull(stack, true);
        }
    }

    private void show(ServerPlayer player, ItemStack stack) {

        int power = ItemAbilityData.getHolyPower(stack);

        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < power; i++) {
            bar.append("■");
        }

        for (int i = power; i < MAX_POWER; i++) {
            bar.append("□");
        }

        AbilityMessages.holyPowerBar(
                player,
                bar.toString(),
                power,
                MAX_POWER
        );
    }

    //护盾功能 (new)
    @Override
    public void use(ServerPlayer player,
                    ItemStack stack,
                    long tick) {

        // 已经在吟唱
        if (ItemAbilityData.isChanneling(stack, tick)) {
            return;
        }

        // 护盾仍然存在
        if (ItemAbilityData.hasShield(stack, tick)) {
            return;
        }

        // 冷却中
        if (ItemAbilityData.isShieldCooling(stack, tick)) {
            return;
        }

        // 开始吟唱
        ItemAbilityData.setChannelStartTick(stack, tick);

        //test
        System.out.println("Channel Start: " + tick);
    }

    private boolean channel(ServerPlayer player,
                            ItemStack stack,
                            long tick) {

        long start = ItemAbilityData.getChannelStartTick(stack);

        // 没在吟唱
        if (start == 0L) {
            return false;
        }

        long elapsed = tick - start;

        System.out.println(
                "tick=" + tick +
                        " start=" + start +
                        " elapsed=" + elapsed
        );

        // 已完成
        if (elapsed >= CHANNEL_TIME) {

            System.out.println("Shield Activated");

            ItemAbilityData.setChannelStartTick(stack, 0L);

            ItemAbilityData.setShieldEndTick(
                    stack,
                    tick + SHIELD_DURATION
            );

            ItemAbilityData.setShieldCooldownEndTick(
                    stack,
                    tick + SHIELD_COOLDOWN
            );

            player.displayClientMessage(
                    Component.literal("Shield Activated!"),
                    false
            );

            return true;
        }


        // 还在吟唱

        int percent = (int) (elapsed * 100L / CHANNEL_TIME);

        player.displayClientMessage(
                Component.literal("Charging... " + percent + "%"),
                true
        );

        return true;
    }
}


