package dev.experiment.hud;

import io.github.apace100.apoli.screen.GameHudRender;
import io.github.apace100.apoli.util.ApoliConfigs;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.LazyOptional;

@OnlyIn(Dist.CLIENT)
public enum ConfiguredHudDrawer implements GameHudRender {
	INSTANCE;
	private static final int BAR_WIDTH = 71;

	@Override
	public void render(GuiGraphics matrixStack, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		LocalPlayer player = client.player;
		LazyOptional<PowerContainer> containerOptional = PowerContainer.get(player);
		if (!containerOptional.isPresent() || player == null)
			return;
		PowerContainer container = containerOptional.orElseThrow(RuntimeException::new);

		int x = client.getWindow().getGuiScaledWidth() / 2 + 20 + ApoliConfigs.CLIENT.resourcesAndCooldowns.hudOffsetX.get();
		int y = client.getWindow().getGuiScaledHeight() - 47 + ApoliConfigs.CLIENT.resourcesAndCooldowns.hudOffsetY.get();
		if (player.getVehicle() instanceof LivingEntity vehicle)
			y -= 8 * (int) (vehicle.getMaxHealth() / 20f);
		if (player.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) || player.getAirSupply() < player.getMaxAirSupply())
			y -= 8;

		for (Holder<ConfiguredPower<?, ?>> power : container.getPowers()) {
			if (!power.isBound())
				return;
			ConfiguredHudRenderer<?, ?> renderer = null; //FIXME Actually implement this.
			DrawType drawType = renderer.shouldDraw(player);
			if (!power.value().shouldRender(player).map(drawType).orElse(false)) continue;
			int height = renderer.height(player);
			float fill = power.value().getFill(player).orElse(0.0F);
			renderer.drawBar(player, matrixStack, x - 2, y - height + 6, BAR_WIDTH, fill);
			renderer.drawIcon(player, matrixStack, x - height - 2, y - height + 6, fill);
			y -= height;
		}
	}
}
