package com.carrot.carrotmod.item;

import com.carrot.carrotmod.CarrotMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class CarrotMaterial {

    public static final int BASE_DURABILITY = 1750;

    public static final TagKey<Item> REPAIRS_CARROT_TOOL =
            TagKey.create(
                    BuiltInRegistries.ITEM.key(),
                    ResourceLocation.fromNamespaceAndPath(CarrotMod.MOD_ID, "carrot_sword_repair")
            );

    public static final ToolMaterial INSTANCE = new ToolMaterial(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, // 挖掘等级（钻石）
            BASE_DURABILITY,                     // 1750
            8.5F,                                // 挖掘速度（钻石与下界合金折中）
            3.5F,                                // 攻击加成（折中）
            13,                                  // 附魔能力（折中）
            REPAIRS_CARROT_TOOL                  // 修复材料
    );
}






