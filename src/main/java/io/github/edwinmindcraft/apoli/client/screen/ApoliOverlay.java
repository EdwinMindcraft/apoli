package io.github.edwinmindcraft.apoli.client.screen;

import io.github.apace100.apoli.screen.GameHudRender;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

public enum ApoliOverlay implements LayeredDraw.Layer {
	INSTANCE;

	@Override
	public void render(GuiGraphics graphics, DeltaTracker partialTicks) {
		if (!Minecraft.getInstance().options.hideGui) {
			for (GameHudRender hudRender : GameHudRender.HUD_RENDERS)
				hudRender.render(graphics, partialTicks);
		}
	}
}
