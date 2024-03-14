package io.github.edwinmindcraft.apoli.api.power.configuration.power;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.util.HudRender;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.power.CooldownPowerFactory;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

/**
 * Represents the necessary configuration to use a {@link CooldownPowerFactory}.
 */
public interface ICooldownPowerConfiguration extends IDynamicFeatureConfiguration {
	int duration();

	HudRender hudRender();

	record Impl(int duration, HudRender hudRender) implements ICooldownPowerConfiguration {}

	MapCodec<ICooldownPowerConfiguration> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "cooldown", 1).forGetter(ICooldownPowerConfiguration::duration),
			ExtraCodecs.strictOptionalField(HudRender.CODEC, "hud_render", HudRender.DONT_RENDER).forGetter(ICooldownPowerConfiguration::hudRender)
	).apply(instance, Impl::new));
}
