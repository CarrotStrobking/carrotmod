package com.carrot.carrotmod.ability.component;

import com.carrot.carrotmod.CarrotMod;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ModDataComponents {

    private ModDataComponents() {}

    public static final DataComponentType<Integer> HOLY_POWER =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    CarrotMod.id("holy_power"),
                    DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .build()
            );

    public static final DataComponentType<Long> LAST_DAMAGE_TICK =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    CarrotMod.id("last_damage_tick"),
                    DataComponentType.<Long>builder()
                            .persistent(Codec.LONG)
                            .build()
            );

    public static final DataComponentType<Long> LAST_RECOVER_TICK =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    CarrotMod.id("last_recover_tick"),
                    DataComponentType.<Long>builder()
                            .persistent(Codec.LONG)
                            .build()
            );

    public static final DataComponentType<Long> NEXT_HEAL_TICK =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    CarrotMod.id("next_heal_tick"),
                    DataComponentType.<Long>builder()
                            .persistent(Codec.LONG)
                            .build()
            );

    public static final DataComponentType<Boolean> WAS_EMPTY =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    CarrotMod.id("was_empty"),
                    DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .build()
            );

    public static final DataComponentType<Boolean> WAS_FULL =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    CarrotMod.id("was_full"),
                    DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .build()
            );

    public static final DataComponentType<Long> CHANNEL_START_TICK =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    CarrotMod.id("channel_start_tick"),
                    DataComponentType.<Long>builder()
                            .persistent(Codec.LONG)
                            .build()
            );

    public static final DataComponentType<Long> SHIELD_END_TICK =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    CarrotMod.id("shield_end_tick"),
                    DataComponentType.<Long>builder()
                            .persistent(Codec.LONG)
                            .build()
            );

    public static final DataComponentType<Long> SHIELD_COOLDOWN_END_TICK =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    CarrotMod.id("shield_cooldown_end_tick"),
                    DataComponentType.<Long>builder()
                            .persistent(Codec.LONG)
                            .build()
            );

    public static void register() {
    }
}
