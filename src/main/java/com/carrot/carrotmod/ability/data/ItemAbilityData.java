package com.carrot.carrotmod.ability.data;

import com.carrot.carrotmod.ability.component.ModDataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class ItemAbilityData {

    private ItemAbilityData() {
    }

    //Holy Power
    public static int getHolyPower(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.HOLY_POWER, 5);
    }

    public static void setHolyPower(ItemStack stack, int power) {
        stack.set(ModDataComponents.HOLY_POWER, Math.max(0, Math.min(5, power)));
    }
    public static long getLastDamageTick(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.LAST_DAMAGE_TICK, 0L);
    }

    public static void setLastDamageTick(ItemStack stack, long tick) {
        stack.set(ModDataComponents.LAST_DAMAGE_TICK, tick);
    }

    public static long getLastRecoverTick(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.LAST_RECOVER_TICK, 0L);
    }

    public static void setLastRecoverTick(ItemStack stack, long tick) {
        stack.set(ModDataComponents.LAST_RECOVER_TICK, tick);
    }

    public static long getNextHealTick(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.NEXT_HEAL_TICK, 0L);
    }

    public static void setNextHealTick(ItemStack stack, long tick) {
        stack.set(ModDataComponents.NEXT_HEAL_TICK, tick);
    }

    public static boolean wasEmpty(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.WAS_EMPTY, false);
    }

    public static void setWasEmpty(ItemStack stack, boolean value) {
        stack.set(ModDataComponents.WAS_EMPTY, value);
    }

    public static boolean wasFull(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.WAS_FULL, true);
    }

    public static void setWasFull(ItemStack stack, boolean value) {
        stack.set(ModDataComponents.WAS_FULL, value);
    }

    //蓄力
    //CHANNEL_START_TICK
    public static long getChannelStartTick(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CHANNEL_START_TICK, 0L);
    }

    public static void setChannelStartTick(ItemStack stack, long tick) {
        stack.set(ModDataComponents.CHANNEL_START_TICK, tick);
    }

    //SHIELD_END_TICK
    public static long getShieldEndTick(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.SHIELD_END_TICK, 0L);
    }

    public static void setShieldEndTick(ItemStack stack, long tick) {
        stack.set(ModDataComponents.SHIELD_END_TICK, tick);
    }

    //SHIELD_COOLDOWN_END_TICK
    public static long getShieldCooldownEndTick(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.SHIELD_COOLDOWN_END_TICK, 0L);
    }

    public static void setShieldCooldownEndTick(ItemStack stack, long tick) {
        stack.set(ModDataComponents.SHIELD_COOLDOWN_END_TICK, tick);
    }

    //状态判断~
    public static boolean isChanneling(ItemStack stack, ServerPlayer player) {
        long start = getChannelStartTick(stack);
        if (start == 0) return false;

        if (!player.isUsingItem()) return false;

        if (player.getUseItem() != stack) return false;

        return true;
    }


    public static boolean hasShield(ItemStack stack, long tick) {
        return tick < getShieldEndTick(stack);
    }

    public static boolean isShieldCooling(ItemStack stack, long tick) {
        return tick < getShieldCooldownEndTick(stack);
    }

    //防止打断提示被反复触发
    public static boolean isChannelInterrupted(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CHANNEL_INTERRUPTED, false);
    }

    public static void setChannelInterrupted(ItemStack stack, boolean value) {
        stack.set(ModDataComponents.CHANNEL_INTERRUPTED, value);
    }

    //防止冷却提示反复触发
    public static long getLastCooldownMessageTick(ItemStack stack) {
        return stack.getOrDefault(
                ModDataComponents.LAST_COOLDOWN_MESSAGE_TICK,
                0L
        );
    }

    public static void setLastCooldownMessageTick(ItemStack stack, long tick) {
        stack.set(
                ModDataComponents.LAST_COOLDOWN_MESSAGE_TICK,
                tick
        );
    }
}
