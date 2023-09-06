package dev.experiment.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import io.github.apace100.apoli.util.HudRender;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.IFactory;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * An object that is used to draw a power bar on the player's screen.
 *
 * @param <T> The configuration of this factory.
 */
public abstract class HudRendererFactory<T extends IDynamicFeatureConfiguration> implements IFactory<T, ConfiguredHudRenderer<T, HudRendererFactory<T>>, HudRendererFactory<T>> {
	public static final Codec<HudRendererFactory<?>> CODEC = ApoliRegistries.codec(HudExperiment.HUD_RENDERERS.registry());

	private final Codec<ConfiguredHudRenderer<T, ?>> codec;

	/**
	 * Creates a new hud renderer factory with the given codec.
	 *
	 * @param codec The codec used to serialize the configuration of this factory.
	 */
	public HudRendererFactory(Codec<T> codec) {
		this.codec = IFactory.singleCodec(IFactory.asMap(codec), this::configure, ConfiguredHudRenderer::getConfiguration);
	}

	public Codec<ConfiguredHudRenderer<T, ?>> getCodec() {
		return this.codec;
	}

	@Override
	public @NotNull ConfiguredHudRenderer<T, HudRendererFactory<T>> configure(@NotNull T input) {
		return new ConfiguredHudRenderer<>(() -> this, input);
	}

	/**
	 * Draws a bar on the players screen, at the given position.
	 *
	 * @param renderer The {@link ConfiguredHudRenderer} to access this factory's configuration.
	 * @param player   The {@link Entity} on whose screen the bar will be drawn.
	 * @param matrices The {@link PoseStack} that represents the root position (top left of the screen).
	 * @param x        The x position to draw this bar at.
	 * @param y        The y position to draw this bar at.
	 * @param width    The width of the bar.
	 * @param fill     The amount of the bar filled ([0,1])
	 */
	@OnlyIn(Dist.CLIENT)
	public abstract void drawBar(ConfiguredHudRenderer<T, ?> renderer, Entity player, GuiGraphics graphics, int x, int y, int width, float fill);

	/**
	 * Draws an icon on the players screen, at the given position.<br/>
	 * The icon is expected to be a square of (height,height) size.
	 *
	 * @param renderer The {@link ConfiguredHudRenderer} to access this factory's configuration.
	 * @param player   The {@link Entity} on whose screen the icon will be drawn.
	 * @param matrices The {@link PoseStack} that represents the root position (top left of the screen).
	 * @param x        The x position to draw this icon at.
	 * @param y        The y position to draw this icon at.
	 * @param fill     The amount of the bar filled ([0,1])
	 */
	@OnlyIn(Dist.CLIENT)
	public abstract void drawIcon(ConfiguredHudRenderer<T, ?> renderer, Entity player, GuiGraphics graphics, int x, int y, float fill);

	/**
	 * Return the state of the drawer.
	 *
	 * @param renderer The {@link ConfiguredHudRenderer} to access this factory's configuration.
	 * @param player   The {@link Entity} on whose screen the hud will be drawn.
	 *
	 * @return The {@link DrawType} representing the expected behaviour for the given parameters.
	 */
	public abstract DrawType shouldDraw(ConfiguredHudRenderer<T, ?> renderer, Entity player);

	/**
	 * Returns the height of this object.
	 * Please note that the height returned here is expected to control both the bar height and the icon's size.
	 *
	 * @param renderer The {@link ConfiguredHudRenderer} to access this factory's configuration.
	 * @param player   The {@link Entity} on whose screen the hud will be drawn.
	 *
	 * @return The height of this object.
	 */
	public abstract int height(ConfiguredHudRenderer<T, ?> renderer, Entity player);

	/**
	 * Since experimental features aren't designed to be loaded most of the time, the legacy hud renderer is still
	 * used, and still requires the {@link HudRender}. This is expected to provide something that is close, while
	 * still being within the limitations of the legacy system. As a rule of thumb, no texture generation should
	 * be done, if it's using multiple textures, pick one.
	 *
	 * @param renderer The {@link ConfiguredHudRenderer} to access this factory's configuration.
	 *
	 * @return The {@link HudRender} that roughly matches this object.
	 */
	public abstract HudRender asStable(ConfiguredHudRenderer<T, ?> renderer);
}
