package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.IntegerComparisonConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.PowerReference;

public record ResourceComparisonConfiguration(IntegerComparisonConfiguration comparison,
											  PowerReference resource) implements IDynamicFeatureConfiguration {
	public static final MapCodec<ResourceComparisonConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			IntegerComparisonConfiguration.CODEC.forGetter(ResourceComparisonConfiguration::comparison),
			PowerReference.codec("resource").forGetter(ResourceComparisonConfiguration::resource)
	).apply(instance, ResourceComparisonConfiguration::new));
}
