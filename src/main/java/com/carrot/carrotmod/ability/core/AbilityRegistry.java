package com.carrot.carrotmod.ability.core;

import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public final class AbilityRegistry {

    private static final Map<Item, Ability> REGISTRY = new HashMap<>();

    private AbilityRegistry() {
    }

    public static void register(Item item, Ability ability) {
        REGISTRY.put(item, ability);
    }

    public static Ability get(Item item) {
        return REGISTRY.get(item);
    }
}