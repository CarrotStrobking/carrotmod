package com.carrot.carrotmod.ability.weapon.carrot;

import com.carrot.carrotmod.ability.core.Ability;
import com.carrot.carrotmod.ability.data.ItemAbilityData;
import com.carrot.carrotmod.ability.data.message.AbilityMessages;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffects;

public class CarrotSwordAbility implements Ability {

    //回血功能
    // Holy Power
    private static final int MAX_POWER = 5;
    private static final int RECOVER_INTERVAL = 160;
    private static final int HEAL_COOLDOWN = 20;
    private static final float HEAL_AMOUNT = 8.0F;

    //护盾功能 (new)
    // Holy Shield
    private static final int CHANNEL_TIME = 60;
    private static final int SHIELD_DURATION = 800;

    //打断类型
    private enum InterruptType {
        DAMAGE,        // 被攻击打断:(一次)
        RELEASE,       // 松开右键(每次)
        SWITCH_HAND,   // 切换主手(每次)
        SLEEP          // 睡觉(每次)
    }

    // 统一打断入口
    private void interruptChannel(ServerPlayer player,
                                  ItemStack stack,
                                  InterruptType type) {

        boolean interruptedBefore = ItemAbilityData.isChannelInterrupted(stack);

        // 被攻击(一次)
        if (type == InterruptType.DAMAGE) {
            if (!interruptedBefore) {
                AbilityMessages.shieldInterrupted(player);
            }
        }
        // 其它类型(每次)
        else {
            AbilityMessages.shieldInterrupted(player);
        }

        // 标记：本次蓄力已经被打断
        ItemAbilityData.setChannelInterrupted(stack, true);

        // 清除蓄力状态
        ItemAbilityData.setChannelStartTick(stack, 0L);
    }

    @Override
    public void tick(ServerPlayer player,
                     ItemStack stack,
                     long tick) {

        // 玩家处于受伤硬直状态（hurtTime > 0），不重复打断，只记录受伤时间用于恢复逻辑
        if (player.hurtTime > 0) {
            ItemAbilityData.setLastDamageTick(stack, tick);
        }

        // 长按右键时自动开始吟唱
        if (player.isUsingItem()
                && player.hurtTime <= 0
                && player.getUseItem() == stack
                && !ItemAbilityData.isChanneling(stack, player)
                && !ItemAbilityData.hasShield(stack, tick)
                && !ItemAbilityData.isShieldCooling(stack, tick)) {

            ItemAbilityData.setChannelStartTick(stack, player.level().getGameTime());

            // 重新蓄力，清除标记
            ItemAbilityData.setChannelInterrupted(stack, false);
        }

        if (channel(player, stack, tick)) {
            return;
        }

        // 护盾
        shieldTick(player, stack, tick);

        // 回血
        heal(player, stack, tick);

        // Holy Power 恢复
        recover(player, stack, tick);

        // 显示 Holy Power bar
        show(player, stack);
    }

    @Override
    public void damaged(ServerPlayer player,
                        ItemStack stack,
                        DamageSource source,
                        float amount,
                        long tick) {

        ItemAbilityData.setLastDamageTick(stack, tick);

        // 受击打断蓄力
        if (ItemAbilityData.isChanneling(stack, player)) {
            interruptChannel(player, stack, InterruptType.DAMAGE);
        }
    }

    //heal
    private void heal(ServerPlayer player, ItemStack stack, long tick) {

        // 必须是主手持有这把剑才允许回血
        if (stack != player.getMainHandItem()) {
            return;
        }

        // 血量高于50%，不回血
        if (player.getHealth() >= player.getMaxHealth() / 2.0F) {
            return;
        }

        int power = ItemAbilityData.getHolyPower(stack);

        // 没充能
        if (power <= 0) {
            return;
        }

        long nextHeal = ItemAbilityData.getNextHealTick(stack);

        // 冷却中
        if (tick < nextHeal) {
            return;
        }

        // 回血
        player.heal(HEAL_AMOUNT);

        // 消耗一层
        int newPower = power - 1;

        ItemAbilityData.setHolyPower(stack, newPower);

        //20tick冷却
        ItemAbilityData.setNextHealTick(stack, tick + HEAL_COOLDOWN);

        if (newPower == 0) {

            AbilityMessages.holyPowerDepleted(player);

            ItemAbilityData.setWasEmpty(stack, true);
            ItemAbilityData.setWasFull(stack, false);
        }
    }

    //recover
    private void recover(ServerPlayer player, ItemStack stack, long tick) {

        if (stack != player.getMainHandItem()) {
            return;
        }

        int power = ItemAbilityData.getHolyPower(stack);

        if (power >= MAX_POWER) {
            return;
        }

        long lastDamage = ItemAbilityData.getLastDamageTick(stack);

        if (tick - lastDamage < RECOVER_INTERVAL) {
            return;
        }

        long lastRecover = ItemAbilityData.getLastRecoverTick(stack);

        if (tick - lastRecover < RECOVER_INTERVAL) {
            return;
        }

        power++;

        ItemAbilityData.setHolyPower(stack, power);
        ItemAbilityData.setLastRecoverTick(stack, tick);

        if (power == 1 && ItemAbilityData.wasEmpty(stack)) {

            AbilityMessages.holyPowerRecovering(player);

            ItemAbilityData.setWasEmpty(stack, false);
        }

        if (power < MAX_POWER) {
            ItemAbilityData.setWasFull(stack, false);
        }

        if (power == MAX_POWER && !ItemAbilityData.wasFull(stack)) {

            AbilityMessages.holyPowerRestored(player);
            ItemAbilityData.setWasFull(stack, true);
        }
    }

    //show
    private void show(ServerPlayer player, ItemStack stack) {

        if (stack != player.getMainHandItem()) {
            return;
        }

        int power = ItemAbilityData.getHolyPower(stack);

        String bar = "■".repeat(Math.max(0, power)) +
                "□".repeat(Math.max(0, MAX_POWER - power));

        AbilityMessages.holyPowerBar(
                player,
                bar,
                power,
                MAX_POWER
        );
    }

    //长按右键吟唱
    @Override
    public void use(ServerPlayer player,
                    ItemStack stack,
                    long tick) {

        if (ItemAbilityData.isChanneling(stack, player)) {
            return;
        }

        if (ItemAbilityData.hasShield(stack, tick)) {
            return;
        }

        if (ItemAbilityData.isShieldCooling(stack, tick)) {
            return;
        }

        ItemAbilityData.setChannelStartTick(stack, player.level().getGameTime());
    }

    //取消吟唱（松开右键）
    @Override
    public void cancelChannel(ServerPlayer player,
                              ItemStack stack) {

        if (!ItemAbilityData.isChanneling(stack, player)) {
            return;
        }

        interruptChannel(player, stack, InterruptType.RELEASE);
    }

    private boolean channel(ServerPlayer player,
                            ItemStack stack,
                            long tick) {

        long start = ItemAbilityData.getChannelStartTick(stack);

        // 没在吟唱
        if (start == 0L) {
            return false;
        }

        //切换主手取消
        if (stack != player.getMainHandItem()) {

            interruptChannel(player, stack, InterruptType.SWITCH_HAND);
            return false;
        }

        //睡觉取消
        if (player.isSleeping()) {

            interruptChannel(player, stack, InterruptType.SLEEP);
            return false;
        }

        long worldTime = player.level().getGameTime();
        long elapsed = worldTime - start;

        //迟缓效果
        player.addEffect(
                new MobEffectInstance(
                        MobEffects.SLOWNESS,
                        5,
                        0,
                        false,
                        false,
                        true
                )
        );

        // 已完成
        if (elapsed >= CHANNEL_TIME) {

            // 必须仍在使用这把剑（右键蓄力中），否则视为被打断
            if (!player.isUsingItem() ||
                    player.getUseItem() != stack) {

                interruptChannel(player, stack, InterruptType.RELEASE);
                return false;
            }

            AbilityMessages.shieldActivated(player);

            ItemAbilityData.setChannelStartTick(stack, 0L);

            // 设置护盾持续时间
            ItemAbilityData.setShieldEndTick(stack, tick + SHIELD_DURATION);

            // 清除打断标记
            ItemAbilityData.setChannelInterrupted(stack, false);

            return true;
        }

        // 还在吟唱
        int percent = (int) (elapsed * 100L / CHANNEL_TIME);

        AbilityMessages.shieldCharging(player, percent);

        return true;
    }

    //死亡取消吟唱
    @Override
    public void death(ServerPlayer player,
                      ItemStack stack) {

        interruptChannel(player, stack, InterruptType.RELEASE);

        ItemAbilityData.setShieldEndTick(stack, 0L);
    }

    // 减伤效果
    @Override
    public float modifyDamageTaken(
            ServerPlayer player,
            ItemStack stack,
            DamageSource source,
            float damage,
            long tick
    ) {
        boolean shield = ItemAbilityData.hasShield(stack, tick);


        if (shield) {
            return damage * 0.8F;
        }

        return damage;
    }


    private void shieldTick(ServerPlayer player,
                            ItemStack stack,
                            long tick) {

        // 没有护盾
        if (!ItemAbilityData.hasShield(stack, tick)) {
            return;
        }

        // 检测主手
        if (stack != player.getMainHandItem()) {

            AbilityMessages.shieldLost(player);

            // 护盾立刻消散
            ItemAbilityData.setShieldEndTick(stack, 0L);

            // 固定冷却 20 秒
            ItemAbilityData.setShieldCooldownEndTick(stack, tick + 400);

            // 清掉蓄力状态，必须重新蓄力
            ItemAbilityData.setChannelStartTick(stack, 0L);
        }
    }
}

