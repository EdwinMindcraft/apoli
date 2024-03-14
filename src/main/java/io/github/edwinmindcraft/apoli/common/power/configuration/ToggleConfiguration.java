package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.TogglePowerConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public record ToggleConfiguration(boolean defaultState, IActivePower.Key key,
								  boolean retainState) implements TogglePowerConfiguration {
	public static final Codec<ToggleConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "active_by_default", true).forGetter(TogglePowerConfiguration::defaultState),
			ExtraCodecs.strictOptionalField(IActivePower.Key.BACKWARD_COMPATIBLE_CODEC, "key", IActivePower.Key.PRIMARY).forGetter(TogglePowerConfiguration::key),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "retain_state", true).forGetter(TogglePowerConfiguration::retainState)
	).apply(instance, ToggleConfiguration::new));
}
