package io.github.apace100.apoli;

import io.github.apace100.apoli.power.factory.condition.EntityConditionsClient;
import io.github.apace100.apoli.power.factory.condition.ItemConditionsClient;
import io.github.apace100.apoli.registry.ApoliClassDataClient;
import io.github.apace100.apoli.screen.GameHudRender;
import io.github.apace100.apoli.screen.PowerHudRenderer;
import io.github.edwinmindcraft.apoli.client.ApoliClientEventHandler;
import io.github.edwinmindcraft.apoli.client.screen.ApoliOverlays;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class ApoliClient {

	public static boolean shouldReloadWorldRenderer = false;
	public static boolean shouldReapplyShaders = false;

	public static void registerPowerKeybinding(String keyId, KeyMapping keyBinding) {
		ApoliClientEventHandler.registerPowerKeybinding(keyId, keyBinding);
	}

	public static void initialize(IEventBus bus) {
		bus.addListener(ApoliClient::setupClient);
		bus.addListener(ApoliOverlays::registerOverlays);

		ApoliClassDataClient.registerAll();

		EntityConditionsClient.register();
		ItemConditionsClient.register();
		GameHudRender.HUD_RENDERS.add(new PowerHudRenderer());
	}

	public static void setupClient(FMLClientSetupEvent event) {
		ApoliOverlays.bootstrap();
	}
}
