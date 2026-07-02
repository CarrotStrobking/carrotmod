package com.carrot.carrotmod.item;

import com.carrot.carrotmod.CarrotMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {

    public static final Item CARROT_EMPIRE_SWORD = registerItem(
            "carrot_empire_sword",
            setting -> new Item(setting.sword(CarrotMaterial.INSTANCE,3.0F,
            -2.4F)));


    private static Item registerItem(String name, Function<Item.Properties, Item> factory) {

        ResourceKey<Item> itemKey = ResourceKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(CarrotMod.MOD_ID, name)
        );

        Item item = factory.apply(
                new Item.Properties().setId(itemKey)
        );

        return Registry.register(
                BuiltInRegistries.ITEM,
                itemKey.location(),
                item
        );
    }

    public static void registerMODItems() {

        CarrotMod.LOGGER.info("Registering Mod Items for" + CarrotMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT)
                .register(entries -> entries.accept(CARROT_EMPIRE_SWORD));
    }

}






