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

    // 玩家最后一次受到伤害的时间
    private static final Map<UUID, Long> LAST_DAMAGE_TIME = new HashMap<>();

    // 当前剩余圣光充能
    private static final Map<UUID, Integer> HOLY_POWER = new HashMap<>();

    // 疲劳结束时间
    private static final Map<UUID, Long> FATIGUE_END = new HashMap<>();

    // 最大圣光充能
    private static final int MAX_CHARGES = 5;

    public static void onPlayerDamaged(ServerPlayer player, long tick) {

        LAST_DAMAGE_TIME.put(
                player.getUUID(),
                tick
        );
    }

    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            long time = server.getTickCount();

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {

                ItemStack stack = player.getMainHandItem();


                // 必须拿着胡萝卜圣剑
                if (stack.getItem() != ModItems.CARROT_EMPIRE_SWORD)
                    continue;

                UUID id = player.getUUID();

                // 初始化圣光充能
                HOLY_POWER.putIfAbsent(id, MAX_CHARGES);


                // ActionBar 显示 Holy Power
                int power = HOLY_POWER.get(id);

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


                // 疲劳状态
                if (FATIGUE_END.containsKey(id)) {

                    if (time >= FATIGUE_END.get(id)) {

                        FATIGUE_END.remove(id);

                        HOLY_POWER.put(id, MAX_CHARGES);

                        player.displayClientMessage(
                                Component.translatable("message.carrotmod.holy_power_restored"),
                                false
                        );

                    } else {

                        // 疲劳期间不能回血
                        continue;
                    }
                }


                // 血量高于一半，不回血
                if (player.getHealth() >= player.getMaxHealth() / 2.0F)
                    continue;


                // 冷却中
                if (player.getCooldowns().isOnCooldown(stack))
                    continue;

                int charges = HOLY_POWER.get(id);

                // 圣光耗尽
                if (charges <= 0) {

                    FATIGUE_END.put(id, time + 300);

                    player.displayClientMessage(
                            Component.translatable("message.carrotmod.holy_power_depleted"),
                            false
                    );

                    player.displayClientMessage(
                            Component.translatable("message.carrotmod.holy_power_recovering"),
                            false
                    );

                    continue;
                }

                // 消耗一次圣光充能
                HOLY_POWER.put(id, charges - 1);

                // 回复4颗心（8生命值）
                player.heal(8.0F);

                // 一秒冷却
                player.getCooldowns().addCooldown(stack, 20);
            }
        });
    }
}
