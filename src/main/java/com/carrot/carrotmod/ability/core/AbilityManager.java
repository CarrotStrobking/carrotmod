package com.carrot.carrotmod.ability.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class AbilityManager {

    private AbilityManager() {}

    public static void tickPlayer(ServerPlayer player, long tick) {

        var inventory = player.getInventory();

        for (int i = 0; i < inventory.getContainerSize(); i++) {

            ItemStack stack = inventory.getItem(i);

            if (stack.isEmpty()) {
                continue;
            }

            Ability ability = AbilityRegistry.get(stack.getItem());

            if (ability == null) {
                continue;
            }

            ability.tick(player, stack, tick);
        }
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

    public static float modifyDamageTaken(
            LivingEntity entity,
            DamageSource source,
            float amount
    ) {

        if (!(entity instanceof ServerPlayer player)) {
            return amount;
        }

        ItemStack stack = player.getMainHandItem();

        Ability ability = AbilityRegistry.get(stack.getItem());

        if (ability == null) {
            return amount;
        }

        float result = ability.modifyDamageTaken(
                player,
                stack,
                source,
                amount,
                player.level().getGameTime()
        );

        return result;
    }

    public static void death(ServerPlayer player) {

        ItemStack stack = player.getMainHandItem();

        if (stack.isEmpty()) return;

        Ability ability = AbilityRegistry.get(stack.getItem());

        if (ability == null) return;

        ability.death(player, stack);
    }

    public static void cancelChannel(ServerPlayer player,
                                     ItemStack stack) {

        Ability ability = AbilityRegistry.get(stack.getItem());

        if (ability == null) {
            return;
        }

        ability.cancelChannel(player, stack);
    }
}
