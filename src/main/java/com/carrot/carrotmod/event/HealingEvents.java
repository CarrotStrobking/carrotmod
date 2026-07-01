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

    // 当前剩余充能
    private static final Map<UUID, Integer> HOLY_CHARGES = new HashMap<>();

    // 疲劳结束时间
    private static final Map<UUID, Long> FATIGUE_END = new HashMap<>();

    private static final int MAX_CHARGES = 5;

    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            long time = server.getTickCount();

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {

                ItemStack stack = player.getMainHandItem();

                // 必须拿着圣剑
                if (stack.getItem() != ModItems.CARROT_EMPIRE_SWORD)
                    continue;

                UUID id = player.getUUID();

                // 初始化充能
                HOLY_CHARGES.putIfAbsent(id, MAX_CHARGES);

                // ==========================
                // 疲劳状态
                // ==========================
                if (FATIGUE_END.containsKey(id)) {

                    if (time >= FATIGUE_END.get(id)) {

                        FATIGUE_END.remove(id);

                        HOLY_CHARGES.put(id, MAX_CHARGES);

                        player.displayClientMessage(
                                Component.translatable("message.carrotmod.holy_power_restored"),
                                true
                        );

                    } else {

                        continue;
                    }
                }

                // 血量高于一半，不回血
                if (player.getHealth() >= player.getMaxHealth() / 2.0F)
                    continue;

                // 冷却
                if (player.getCooldowns().isOnCooldown(stack))
                    continue;

                int charges = HOLY_CHARGES.get(id);

                // 没有充能
                if (charges <= 0) {

                    FATIGUE_END.put(id, time + 300);

                    player.displayClientMessage(
                            Component.translatable("message.carrotmod.holy_power_depleted"),
                            true
                    );

                    player.displayClientMessage(
                            Component.translatable("message.carrotmod.holy_power_recovering"),
                            false
                    );

                    continue;
                }

                // 消耗一次充能
                HOLY_CHARGES.put(id, charges - 1);

                // 回复3.5颗心（7生命值）
                player.heal(8.0F);

                // 一秒冷却
                player.getCooldowns().addCooldown(stack, 20);
            }
        });
    }
}
