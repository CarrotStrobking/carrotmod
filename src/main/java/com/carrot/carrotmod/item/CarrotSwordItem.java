package com.carrot.carrotmod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class CarrotSwordItem extends Item {

    public CarrotSwordItem(
            ToolMaterial material,
            float attackDamage,
            float attackSpeed,
            Properties properties) {

        super(properties.sword(material, attackDamage, attackSpeed));
    }
}