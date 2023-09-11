package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public record BurnConfiguration(int interval, int duration) implements IDynamicFeatureConfiguration {
	public static final Codec<BurnConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("interval").forGetter(BurnConfiguration::interval),
			CalioCodecHelper.INT.fieldOf("burn_duration").forGetter(BurnConfiguration::duration)
	).apply(instance, BurnConfiguration::new));
}
