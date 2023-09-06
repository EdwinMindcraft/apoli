package dev.experiment.hud.factory;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.experiment.hud.ConfiguredHudRenderer;
import dev.experiment.hud.DrawType;
import dev.experiment.hud.HudRendererFactory;
import io.github.apace100.apoli.util.HudRender;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DefaultHudRenderer extends HudRendererFactory<HudRender> {

	public DefaultHudRenderer() {
		super(HudRender.CODEC);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawBar(ConfiguredHudRenderer<HudRender, ?> renderer, Entity player, GuiGraphics graphics, int x, int y, int width, float fill) {
		HudRender render = renderer.getConfiguration();
		ResourceLocation currentLocation = render.spriteLocation();
		RenderSystem.setShaderTexture(0, currentLocation);
		int v = 8 + render.barIndex() * 10;
		if (render.isInverted())
			fill = 1f - fill;
		int w = (int) (fill * width);
		graphics.blit(currentLocation, x, y, 0, 0, 0, width, 5, 256, 256);
		graphics.blit(currentLocation, x, y, 0, 0, v, w, 8, 256, 256);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawIcon(ConfiguredHudRenderer<HudRender, ?> renderer, Entity player, GuiGraphics graphics, int x, int y, float fill) {
		HudRender render = renderer.getConfiguration();
		int v = 8 + render.barIndex() * 10;
		graphics.blit(render.spriteLocation(), x, y, 1, 73, v, 8, 8, 256, 256);
	}

	@Override
	public DrawType shouldDraw(ConfiguredHudRenderer<HudRender, ?> renderer, Entity player) {
		return renderer.getConfiguration().shouldRender(player) ? DrawType.SHOW : DrawType.HIDE;
	}

	@Override
	public int height(ConfiguredHudRenderer<HudRender, ?> renderer, Entity player) {
		return 8;
	}

	@Override
	public HudRender asStable(ConfiguredHudRenderer<HudRender, ?> renderer) {
		return renderer.getConfiguration();
	}
}
