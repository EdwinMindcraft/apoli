package dev.experiment.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import dev.experiment.helper.ExperimentCodec;
import io.github.apace100.apoli.util.ApoliConfigs;
import io.github.apace100.apoli.util.HudRender;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConfiguredFactory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/**
 * Represents a more versatile version of the {@link HudRender} provided by fabric.
 * TODO: Enable support for {@link net.minecraft.core.Holder} based serialisation.
 *
 * @param <T> The type of the configuration.
 * @param <F> The type of the factory.
 */
public final class ConfiguredHudRenderer<T extends IDynamicFeatureConfiguration, F extends HudRendererFactory<T>> extends ConfiguredFactory<T, F, ConfiguredHudRenderer<T, F>> {
	public static final Codec<ConfiguredHudRenderer<?, ?>> BASE_CODEC = HudRendererFactory.CODEC.dispatch(ConfiguredHudRenderer::getFactory, HudRendererFactory::getCodec);
	public static final Codec<ConfiguredHudRenderer<?, ?>> LEGACY_CODEC = HudRender.MAP_CODEC.<ConfiguredHudRenderer<?, ?>>xmap(render -> HudExperiment.DEFAULT.get().configure(render), ConfiguredHudRenderer::asStable).codec();
	public static final Codec<ConfiguredHudRenderer<?, ?>> CODEC = new ExperimentCodec<>(LEGACY_CODEC, BASE_CODEC, HudExperiment::isEnabled);

	public ConfiguredHudRenderer(Supplier<F> factory, T configuration) {
		super(factory, configuration);
	}

	@OnlyIn(Dist.CLIENT)
	public void drawBar(Entity player, GuiGraphics graphics, int x, int y, int width, float fill) {
		this.getFactory().drawBar(this, player, graphics, x, y, width, fill);
	}

	@OnlyIn(Dist.CLIENT)
	public void drawIcon(Entity player, GuiGraphics graphics, int x, int y, float fill) {
		this.getFactory().drawIcon(this, player, graphics, x, y, fill);
	}

	public DrawType shouldDraw(Entity player) {
		return this.getFactory().shouldDraw(this, player);
	}

	public int height(Entity player) {
		return this.getFactory().height(this, player);
	}

	public HudRender asStable() {
		return this.getFactory().asStable(this);
	}
}
