package com.carrot.carrotmod;
import com.carrot.carrotmod.event.HealingEvents;
import com.carrot.carrotmod.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarrotMod implements ModInitializer {
	public static final String MOD_ID = "carrotmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModItems.registerMODItems();

		HealingEvents.register();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
