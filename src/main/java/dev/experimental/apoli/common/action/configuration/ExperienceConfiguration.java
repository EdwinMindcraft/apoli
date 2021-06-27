package dev.experimental.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.experimental.apoli.api.IDynamicFeatureConfiguration;

public record ExperienceConfiguration(int points, int levels) implements IDynamicFeatureConfiguration {

	public static final Codec<ExperienceConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("points", 0).forGetter(ExperienceConfiguration::points),
			Codec.INT.optionalFieldOf("levels", 0).forGetter(ExperienceConfiguration::levels)
	).apply(instance, ExperienceConfiguration::new));
}
