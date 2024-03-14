package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.TogglePowerConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public record ToggleNightVisionConfiguration(TogglePowerConfiguration toggle,
											 float strength) implements TogglePowerConfiguration.Wrapper {
	public static final Codec<ToggleNightVisionConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			TogglePowerConfiguration.INACTIVE_MAP_CODEC.forGetter(ToggleNightVisionConfiguration::toggle),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.FLOAT, "strength", 1.0F).forGetter(ToggleNightVisionConfiguration::strength)
	).apply(instance, ToggleNightVisionConfiguration::new));

	public ToggleNightVisionConfiguration(boolean defaultState, IActivePower.Key key, float strength) {
		this(new Impl(defaultState, key, true), strength);
	}

	@Override
	public TogglePowerConfiguration wrapped() {
		return this.toggle();
	}
}
