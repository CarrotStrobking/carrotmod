package com.carrot.carrotmod;

import com.carrot.carrotmod.ability.component.ModDataComponents;
import com.carrot.carrotmod.ability.core.AbilityRegistry;
import com.carrot.carrotmod.ability.weapon.carrot.CarrotSwordAbility;
import com.carrot.carrotmod.event.core.ModEvents;
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

		// 注册能力系统
		AbilityRegistry.register(ModItems.CARROT_EMPIRE_SWORD, new CarrotSwordAbility());
		ModEvents.register();
		ModDataComponents.register();
		ModEvents.register();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
