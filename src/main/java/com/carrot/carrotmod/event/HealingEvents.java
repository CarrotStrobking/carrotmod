package com.carrot.carrotmod.event;

import com.carrot.carrotmod.item.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HealingEvents {

    // 状态定义
    public enum HolyState {
        FULL,        // 满
        ACTIVE,      // 有能量
        EMPTY        // 用完
    }


    // 数据存储
    private static final Map<UUID, HolyState> STATE = new HashMap<>();
    private static final Map<UUID, Integer> POWER = new HashMap<>();

    private static final Map<UUID, Long> LAST_DAMAGE = new HashMap<>();
    private static final Map<UUID, Long> LAST_RECOVER = new HashMap<>();
    private static final Map<UUID, Long> NEXT_HEAL = new HashMap<>();


    // 常量
    private static final int MAX_CHARGES = 5;
    private static final int RECOVER_INTERVAL = 160; // 8s
    private static final int HEAL_COOLDOWN = 20;      // 1s
    private static final float HEAL_AMOUNT = 8.0F;


    // 受伤事件
    public static void onPlayerDamaged(ServerPlayer player, long tick) {

        UUID id = player.getUUID();

        LAST_DAMAGE.put(id, tick);
        LAST_RECOVER.put(id, tick);

        // 受伤时更新状态
        int power = POWER.getOrDefault(id, MAX_CHARGES);

        if (power <= 0) {
            STATE.put(id, HolyState.EMPTY);
        } else {
            STATE.put(id, HolyState.ACTIVE);
        }
    }


    // 注册 Tick
    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            long time = server.getTickCount();

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {

                ItemStack stack = player.getMainHandItem();

                if (stack.getItem() != ModItems.CARROT_EMPIRE_SWORD)
                    continue;

                UUID id = player.getUUID();


                // 初始化
                STATE.putIfAbsent(id, HolyState.FULL);
                POWER.putIfAbsent(id, MAX_CHARGES);

                LAST_DAMAGE.putIfAbsent(id, time);
                LAST_RECOVER.putIfAbsent(id, time);
                NEXT_HEAL.putIfAbsent(id, 0L);


                // 主逻辑
                recover(id, time, player);
                tryHeal(player, id, time);
                show(player);
            }
        });
    }


    // 显示 UI
    private static void show(ServerPlayer player) {

        UUID id = player.getUUID();

        int power = POWER.getOrDefault(id, MAX_CHARGES);

        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < power; i++) bar.append("■");
        for (int i = power; i < MAX_CHARGES; i++) bar.append("□");

        player.displayClientMessage(
                Component.translatable(
                        "message.carrotmod.holy_power_bar",
                        bar.toString(),
                        power,
                        MAX_CHARGES
                ),
                true
        );
    }


    // 恢复逻辑（核心修复点）
    private static void recover(UUID id, long time, ServerPlayer player) {

        int power = POWER.getOrDefault(id, MAX_CHARGES);

        if (power >= MAX_CHARGES) {
            STATE.put(id, HolyState.FULL);
            return;
        }

        long lastDamage = LAST_DAMAGE.getOrDefault(id, time);
        if (time - lastDamage < RECOVER_INTERVAL) return;

        long lastRecover = LAST_RECOVER.getOrDefault(id, time);
        if (time - lastRecover < RECOVER_INTERVAL) return;

        int oldPower = power;

        power++;

        POWER.put(id, power);
        LAST_RECOVER.put(id, time);

        STATE.put(id, HolyState.ACTIVE);


        if (oldPower == 0 && power == 1) {
            player.displayClientMessage(
                    Component.translatable("message.carrotmod.holy_power_recovering"),
                    false
            );
        }


        if (oldPower < MAX_CHARGES && power == MAX_CHARGES) {
            player.displayClientMessage(
                    Component.translatable("message.carrotmod.holy_power_restored"),
                    false
            );

            STATE.put(id, HolyState.FULL);
        }
    }


    // 自动回血逻辑
    private static void tryHeal(ServerPlayer player, UUID id, long time) {

        if (player.getHealth() >= player.getMaxHealth() / 2.0F)
            return;

        int power = POWER.getOrDefault(id, MAX_CHARGES);

        if (power <= 0) {
            STATE.put(id, HolyState.EMPTY);
            return;
        }

        long next = NEXT_HEAL.getOrDefault(id, 0L);
        if (time < next) return;

        POWER.put(id, power - 1);

        player.heal(HEAL_AMOUNT);

        NEXT_HEAL.put(id, time + HEAL_COOLDOWN);

        // 消耗后状态更新
        if (power - 1 <= 0) {
            STATE.put(id, HolyState.EMPTY);
        } else {
            STATE.put(id, HolyState.ACTIVE);
        }
    }
}

