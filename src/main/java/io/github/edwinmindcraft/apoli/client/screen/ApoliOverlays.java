package io.github.edwinmindcraft.apoli.client.screen;

import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.common.power.configuration.OverlayConfiguration;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class ApoliOverlays {
	public static void bootstrap() {

	}

	public static void registerOverlays(RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.HOTBAR, ApoliAPI.identifier("overlay"), ApoliOverlay.INSTANCE);
		event.registerBelowAll(ApoliAPI.identifier("bottom_overlay"), new ApoliPowerOverlay(configuration -> configuration.phase() == OverlayConfiguration.DrawPhase.BELOW_HUD));
		event.registerAboveAll(ApoliAPI.identifier("above_overlay"), new ApoliPowerOverlay(configuration -> configuration.phase() == OverlayConfiguration.DrawPhase.ABOVE_HUD));
	}
}
