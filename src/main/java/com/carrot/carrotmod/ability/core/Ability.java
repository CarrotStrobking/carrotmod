package com.carrot.carrotmod.ability.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface Ability {

    default void tick(ServerPlayer player, ItemStack stack, long tick) {}

    default void use(ServerPlayer player, ItemStack stack,long tick) {}

    default void attack(ServerPlayer player, ItemStack stack, LivingEntity target) {}

    default void damaged(ServerPlayer player,
                         ItemStack stack,
                         DamageSource source,
                         float amount,
                         long tick) {}

    default float modifyDamageTaken(ServerPlayer player,
                                    ItemStack stack,
                                    DamageSource source,
                                    float damage,
                                    long tick) {
        return damage;
    }

    default void death(ServerPlayer player,
                       ItemStack stack) {
    }

    default void cancelChannel(ServerPlayer player,
                               ItemStack stack) {
    }
}
