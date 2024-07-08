package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.DoubleComparisonConfiguration;

public record ScoreboardComparisonConfiguration(DoubleComparisonConfiguration comparison,
												String objective) implements IDynamicFeatureConfiguration {
	public static final MapCodec<ScoreboardComparisonConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			DoubleComparisonConfiguration.CODEC.forGetter(ScoreboardComparisonConfiguration::comparison),
			Codec.STRING.fieldOf("objective").forGetter(ScoreboardComparisonConfiguration::objective)
	).apply(instance, ScoreboardComparisonConfiguration::new));
}
