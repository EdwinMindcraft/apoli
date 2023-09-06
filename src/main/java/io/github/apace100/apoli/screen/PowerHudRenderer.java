package io.github.apace100.apoli.screen;

import io.github.apace100.apoli.util.ApoliConfigs;
import io.github.apace100.apoli.util.HudRender;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;

import java.util.Comparator;
import java.util.List;

public class PowerHudRenderer implements GameHudRender {

	@Override
	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics context, float delta) {
		Minecraft client = Minecraft.getInstance();
		LocalPlayer player = client.player;
		if (player == null)
			return;
		IPowerContainer.get(player).ifPresent(component -> {
			int x = client.getWindow().getGuiScaledWidth() / 2 + 20 + ApoliConfigs.CLIENT.resourcesAndCooldowns.hudOffsetX.get();
			int y = client.getWindow().getGuiScaledHeight() - 47 + ApoliConfigs.CLIENT.resourcesAndCooldowns.hudOffsetY.get();
			if (player.getVehicle() instanceof LivingEntity vehicle)
				y -= 8 * (int) (vehicle.getMaxHealth() / 20f);
			if (player.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) || player.getAirSupply() < player.getMaxAirSupply()) {
				y -= 8;
			}
			int barWidth = 71;
			int barHeight = 8;
			int iconSize = 8;
			List<? extends ConfiguredPower<?, ?>> configuredPowers = component.getPowers().stream().map(Holder::value)
					.filter(power -> power.asHudRendered().isPresent())
					.sorted(Comparator.comparing(power -> power.getRenderSettings(player).orElse(HudRender.DONT_RENDER).spriteLocation()))
					.toList();
			//ResourceLocation lastLocation = null;
			//RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			for (ConfiguredPower<?, ?> hudPower : configuredPowers) {
				HudRender render = hudPower.getRenderSettings(player).orElse(HudRender.DONT_RENDER);
				if (render.shouldRender(player) && hudPower.shouldRender(player).orElse(false)) {
					ResourceLocation currentLocation = render.spriteLocation();
					/*if (currentLocation != lastLocation) {
						RenderSystem.setShaderTexture(0, currentLocation);
						lastLocation = currentLocation;
					}
					 */
                    context.blit(currentLocation, x, y, 0, 0, barWidth, 5);
					int v = 8 + render.barIndex() * 10;
					float fill = hudPower.getFill(player).orElse(0.0F);
					if (render.isInverted()) {
						fill = 1f - fill;
					}
					int w = (int) (fill * barWidth);
                    context.blit(currentLocation, x, y - 2, 0, v, w, barHeight);
					// this.setBlitOffset(this.getBlitOffset() + 1);
                    context.blit(currentLocation, x - iconSize - 2, y - 2, 73, v, iconSize, iconSize);
					// this.setBlitOffset(this.getBlitOffset() - 1);
					y -= 8;
				}
			}
		});
	}
}