package io.github.apace100.apoli.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface GameHudRender {

	List<GameHudRender> HUD_RENDERS = new ArrayList<>();

	void render(GuiGraphics matrixStack, float tickDelta);
}