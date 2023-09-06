package io.github.edwinmindcraft.apoli.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.OverlayPower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ColorConfiguration;
import io.github.edwinmindcraft.apoli.common.power.configuration.OverlayConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.List;
import java.util.function.Predicate;

public class ApoliPowerOverlay implements IGuiOverlay {
	private final Predicate<OverlayConfiguration> shouldDraw;

	public ApoliPowerOverlay(Predicate<OverlayConfiguration> shouldDraw) {
		this.shouldDraw = shouldDraw;
	}

	private void renderPower(ConfiguredPower<OverlayConfiguration, ?> power, GuiGraphics graphics, int width, int height, float partialTick) {
		OverlayConfiguration configuration = power.getConfiguration();
		ColorConfiguration color = configuration.color();

		double d, overlayWidth, overlayHeight, x, y;

		switch (configuration.mode()) {
			case NAUSEA:
				color = color.multiply(configuration.strength()).withAlpha(1.0F);
				d = Mth.lerp(configuration.strength(), 2.0D, 1.0D);
				overlayWidth = (double) width * d;
				overlayHeight = (double) height * d;
				x = ((double) width - overlayWidth) / 2.0D;
				y = ((double) height - overlayHeight) / 2.0D;
				break;
			case TEXTURE:
			default:
				color = color.withAlpha(configuration.strength());
				overlayWidth = width;
				overlayHeight = height;
				x = 0;
				y = 0;
				break;
		}

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		switch (configuration.mode()) {
			case NAUSEA:
				RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
				break;
			case TEXTURE:
			default:
				RenderSystem.defaultBlendFunc();
				break;
		}
		RenderSystem.setShaderColor(color.red(), color.green(), color.blue(), color.alpha());
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, configuration.texture());
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tesselator.getBuilder();
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferBuilder.vertex(x, y + overlayHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
		bufferBuilder.vertex(x + overlayWidth, y + overlayHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
		bufferBuilder.vertex(x + overlayWidth, y, -90.0D).uv(1.0F, 0.0F).endVertex();
		bufferBuilder.vertex(x, y, -90.0D).uv(0.0F, 0.0F).endVertex();
		tesselator.end();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
	}

	@Override
	public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
		boolean hideGui = Minecraft.getInstance().options.hideGui;
		boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
		List<ConfiguredPower<OverlayConfiguration, OverlayPower>> powers = IPowerContainer.getPowers(Minecraft.getInstance().getCameraEntity(), ApoliPowers.OVERLAY.get()).stream()
				.map(Holder::value)
				.filter(x -> this.shouldDraw.test(x.getConfiguration()) && (!x.getConfiguration().hideWithHud() || !hideGui) && (x.getConfiguration().visibleInThirdPerson() || isFirstPerson))
				.toList();
		for (ConfiguredPower<OverlayConfiguration, OverlayPower> power : powers) {
			this.renderPower(power, graphics, width, height, partialTick);
		}
	}
}
