package com.carrot.carrotmod.ability.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class AbilityManager {

    private AbilityManager() {}

    public static void tickPlayer(ServerPlayer player, long tick) {
        tick(player, player.getMainHandItem(), tick);
    }

    public static void use(ServerPlayer player,
                           ItemStack stack,
                           long tick) {

        Ability ability = AbilityRegistry.get(stack.getItem());

        if (ability == null) return;

        ability.use(player, stack, tick);
    }

    public static void attack(ServerPlayer player,
                              ItemStack stack,
                              LivingEntity target) {

        Ability ability = AbilityRegistry.get(stack.getItem());

        if (ability == null) return;

        ability.attack(player, stack, target);
    }

    public static void damaged(ServerPlayer player,
                               DamageSource source,
                               float amount,
                               long tick) {

        ItemStack stack = player.getMainHandItem();

        if (stack.isEmpty()) return;

        Ability ability = AbilityRegistry.get(stack.getItem());

        if (ability == null) return;

        ability.damaged(player, stack, source, amount, tick);
    }

    private static void tick(ServerPlayer player,
                             ItemStack stack,
                             long tick) {

        if (stack.isEmpty()) return;

        Ability ability = AbilityRegistry.get(stack.getItem());

        if (ability == null) return;

        ability.tick(player, stack, tick);
    }
}
