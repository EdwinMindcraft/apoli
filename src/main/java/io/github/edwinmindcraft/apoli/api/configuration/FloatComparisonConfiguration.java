package io.github.edwinmindcraft.apoli.api.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public record FloatComparisonConfiguration(Comparison comparison,
										   float compareTo) implements IDynamicFeatureConfiguration {
	public static final MapCodec<FloatComparisonConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ApoliDataTypes.COMPARISON.fieldOf("comparison").forGetter(FloatComparisonConfiguration::comparison),
			CalioCodecHelper.FLOAT.fieldOf("compare_to").forGetter(FloatComparisonConfiguration::compareTo)
	).apply(instance, FloatComparisonConfiguration::new));

	public boolean check(float value) {
		return this.comparison().compare(value, this.compareTo());
	}
}
