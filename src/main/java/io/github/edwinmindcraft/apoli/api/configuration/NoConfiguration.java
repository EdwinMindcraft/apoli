package io.github.edwinmindcraft.apoli.api.configuration;

import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;

public record NoConfiguration() implements IDynamicFeatureConfiguration {
	public static final NoConfiguration INSTANCE = new NoConfiguration();
	public static final MapCodec<NoConfiguration> CODEC = MapCodec.unit(new NoConfiguration());
}
