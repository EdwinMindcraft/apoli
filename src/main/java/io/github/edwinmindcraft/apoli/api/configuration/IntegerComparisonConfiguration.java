package io.github.edwinmindcraft.apoli.api.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

import java.util.Optional;

public record IntegerComparisonConfiguration(Comparison comparison,
											 int compareTo) implements IDynamicFeatureConfiguration {
	public static final MapCodec<IntegerComparisonConfiguration> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ApoliDataTypes.COMPARISON.fieldOf("comparison").forGetter(IntegerComparisonConfiguration::comparison),
			Codec.INT.fieldOf("compare_to").forGetter(IntegerComparisonConfiguration::compareTo)
	).apply(instance, IntegerComparisonConfiguration::new));

	public static MapCodec<IntegerComparisonConfiguration> withDefaults(Comparison comparison, int value) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				CalioCodecHelper.optionalField(ApoliDataTypes.COMPARISON, "comparison", comparison).forGetter(IntegerComparisonConfiguration::comparison),
				CalioCodecHelper.optionalField(Codec.INT,"compare_to", value).forGetter(IntegerComparisonConfiguration::compareTo)
		).apply(instance, IntegerComparisonConfiguration::new));
	}

	public static final MapCodec<Optional<IntegerComparisonConfiguration>> OPTIONAL_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			CalioCodecHelper.optionalField(ApoliDataTypes.COMPARISON, "comparison").forGetter(x -> x.map(IntegerComparisonConfiguration::comparison)),
			CalioCodecHelper.optionalField(Codec.INT, "compare_to").forGetter(x -> x.map(IntegerComparisonConfiguration::compareTo))
	).apply(instance, (t1, t2) -> t1.flatMap(x1 -> t2.map(x2 -> new IntegerComparisonConfiguration(x1, x2)))));

	public static final Codec<IntegerComparisonConfiguration> CODEC = MAP_CODEC.codec();

	public boolean check(int value) {
		return this.comparison().compare(value, this.compareTo());
	}

	public int getOptimalStoppingPoint() {
		return this.comparison().getOptimalStoppingIndex(this.compareTo());
	}

	public IntegerComparisonConfiguration inverse() {
		return new IntegerComparisonConfiguration(this.comparison().inverse(), this.compareTo());
	}
}
