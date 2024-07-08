package io.github.edwinmindcraft.apoli.api.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public record DoubleComparisonConfiguration(Comparison comparison,
											double compareTo) implements IDynamicFeatureConfiguration {
	public static final MapCodec<DoubleComparisonConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ApoliDataTypes.COMPARISON.fieldOf("comparison").forGetter(DoubleComparisonConfiguration::comparison),
			CalioCodecHelper.DOUBLE.fieldOf("compare_to").forGetter(DoubleComparisonConfiguration::compareTo)
	).apply(instance, DoubleComparisonConfiguration::new));

	public boolean check(double value) {
		return this.comparison().compare(value, this.compareTo());
	}
}
