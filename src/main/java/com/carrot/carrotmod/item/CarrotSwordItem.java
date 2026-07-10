package com.carrot.carrotmod.item;

import com.carrot.carrotmod.ability.core.AbilityManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;

public class CarrotSwordItem extends Item {

    public CarrotSwordItem(
            ToolMaterial material,
            float attackDamage,
            float attackSpeed,
            Properties properties) {

        super(properties.sword(material, attackDamage, attackSpeed));
    }

    @Override
    public InteractionResult use(Level level,
                                 Player player,
                                 InteractionHand hand) {

        player.startUsingItem(hand);

        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(ItemStack stack,
                              LivingEntity entity) {
        return 72000;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.NONE;
    }

    @Override
    public boolean releaseUsing(ItemStack stack,
                                Level level,
                                LivingEntity entity,
                                int timeLeft) {

        if (!level.isClientSide() && entity instanceof ServerPlayer player) {
            AbilityManager.cancelChannel(player, stack);
        }

        return false;
    }

    @Override
    public void onUseTick(Level level,
                          LivingEntity entity,
                          ItemStack stack,
                          int remainingUseTicks) {

        if (level.isClientSide()) {
            return;
        }

        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        AbilityManager.use(
                player,
                stack,
                level.getGameTime()
        );
    }

}