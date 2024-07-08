package io.github.edwinmindcraft.apoli.compat;

import io.github.edwinmindcraft.apoli.compat.citadel.CitadelEventHandler;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;

public class ApoliCompat {
	public static void apply() {
		if (ModList.get().isLoaded("citadel")) applyCitadelCompat();
	}

	private static void applyCitadelCompat() {
		NeoForge.EVENT_BUS.register(new CitadelEventHandler());
	}
}
