package io.github.apace100.apoli.screen;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public interface GameHudRender {

	List<GameHudRender> HUD_RENDERS = new ArrayList<>();

	void render(GuiGraphics matrixStack, DeltaTracker tickDelta);
}