package com.carrot.carrotmod.event;

import com.carrot.carrotmod.item.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HealingEvents {

    // ==========================
    // 玩家最后一次受到伤害的时间
    // ==========================
    private static final Map<UUID, Long> LAST_DAMAGE_TIME = new HashMap<>();

    // ==========================
    // 上一次 Holy Power 恢复时间
    // ==========================
    private static final Map<UUID, Long> LAST_RECOVER_TIME = new HashMap<>();

    // ==========================
    // 下一次允许回血时间
    // ==========================
    private static final Map<UUID, Long> NEXT_HEAL_TIME = new HashMap<>();

    // ==========================
    // 当前 Holy Power
    // ==========================
    private static final Map<UUID, Integer> HOLY_POWER = new HashMap<>();

    // ==========================
    // 是否已经提示过圣光耗尽
    // 防止聊天栏刷屏
    // ==========================
    private static final Map<UUID, Boolean> EMPTY_NOTIFIED = new HashMap<>();

    // ==========================
    // 最大 Holy Power
    // ==========================
    private static final int MAX_CHARGES = 5;

    // ==========================
    // 每层 Holy Power 恢复时间（8 秒）
    // ==========================
    private static final int RECOVER_INTERVAL = 160;

    // ==========================
    // 回血冷却（1 秒）
    // ==========================
    private static final int HEAL_INTERVAL = 20;

    // ==========================
    // 每次回血量（4颗心）
    // ==========================
    private static final float HEAL_AMOUNT = 8.0F;

    /**
     * 玩家受到伤害时由 Mixin 调用
     */
    public static void onPlayerDamaged(ServerPlayer player, long tick) {

        UUID id = player.getUUID();

        LAST_DAMAGE_TIME.put(id, tick);

        LAST_RECOVER_TIME.put(id, tick);

        // 再次允许显示"圣光耗尽"
        EMPTY_NOTIFIED.put(id, false);
    }

    /**
     * 注册 Tick 事件
     */
    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            long time = server.getTickCount();

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {

                // 必须手持胡萝卜圣剑
                if (player.getMainHandItem().getItem() != ModItems.CARROT_EMPIRE_SWORD)
                    continue;

                UUID id = player.getUUID();

                // 初始化数据
                HOLY_POWER.putIfAbsent(id, MAX_CHARGES);
                LAST_DAMAGE_TIME.putIfAbsent(id, time);
                LAST_RECOVER_TIME.putIfAbsent(id, time);
                NEXT_HEAL_TIME.putIfAbsent(id, 0L);
                EMPTY_NOTIFIED.putIfAbsent(id, false);

                // Holy Power 自动恢复
                recoverHolyPower(player, id, time);

                // 自动回血
                tryHeal(player, id, time);

                // 最后刷新 ActionBar
                showHolyPower(player);
            }
        });
    }

    /**
     * ActionBar 显示 Holy Power
     */
    private static void showHolyPower(ServerPlayer player) {

        int power = HOLY_POWER.get(player.getUUID());

        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < power; i++)
            bar.append("■");

        for (int i = power; i < MAX_CHARGES; i++)
            bar.append("□");

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

    /**
     * Holy Power 自动恢复
     * 连续8秒未受到伤害恢复1层
     */
    private static void recoverHolyPower(ServerPlayer player,
                                         UUID id,
                                         long time) {

        int power = HOLY_POWER.get(id);

        // 已满
        if (power >= MAX_CHARGES)
            return;

        // 最近受到伤害
        if (time - LAST_DAMAGE_TIME.get(id) < RECOVER_INTERVAL)
            return;

        // 上一层恢复不足8秒
        if (time - LAST_RECOVER_TIME.get(id) < RECOVER_INTERVAL)
            return;

        power++;

        HOLY_POWER.put(id, power);

        LAST_RECOVER_TIME.put(id, time);

        EMPTY_NOTIFIED.put(id, false);

        // 恢复第一层
        if (power == 1) {

            player.displayClientMessage(
                    Component.translatable("message.carrotmod.holy_power_partial"),
                    false
            );
        }

        // 完全恢复
        if (power == MAX_CHARGES) {

            player.displayClientMessage(
                    Component.translatable("message.carrotmod.holy_power_restored"),
                    false
            );
        }
    }
    /**
     * 自动回血
     * 半血以下自动触发
     */
    private static void tryHeal(ServerPlayer player,
                                UUID id,
                                long time) {

        // ==========================
        // 半血以上不回血
        // ==========================
        if (player.getHealth() >= player.getMaxHealth() / 2.0F)
            return;

        int power = HOLY_POWER.get(id);

        // ==========================
        // Holy Power 已耗尽
        // ==========================
        if (power <= 0) {

            // 只提示一次
            if (!EMPTY_NOTIFIED.get(id)) {

                player.displayClientMessage(
                        Component.translatable("message.carrotmod.holy_power_depleted"),
                        false
                );

                player.displayClientMessage(
                        Component.translatable("message.carrotmod.holy_power_recovering"),
                        false
                );

                EMPTY_NOTIFIED.put(id, true);
            }

            return;
        }

        // ==========================
        // 回血冷却（1 秒）
        // ==========================
        if (time < NEXT_HEAL_TIME.get(id))
            return;

        // ==========================
        // 消耗 Holy Power
        // ==========================
        HOLY_POWER.put(id, power - 1);

        // ==========================
        // 回复生命值（4颗心）
        // ==========================
        player.heal(HEAL_AMOUNT);

        // ==========================
        // 设置下一次回血时间
        // ==========================
        NEXT_HEAL_TIME.put(id, time + HEAL_INTERVAL);
    }
}


