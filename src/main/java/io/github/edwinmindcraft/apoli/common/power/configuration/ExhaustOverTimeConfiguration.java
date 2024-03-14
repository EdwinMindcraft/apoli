package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public record ExhaustOverTimeConfiguration(int interval, float exhaustion) implements IDynamicFeatureConfiguration {
	public static final Codec<ExhaustOverTimeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(Codec.intRange(1, Integer.MAX_VALUE), "interval", 20).forGetter(ExhaustOverTimeConfiguration::interval),
			CalioCodecHelper.FLOAT.fieldOf("exhaustion").forGetter(ExhaustOverTimeConfiguration::exhaustion)
	).apply(instance, ExhaustOverTimeConfiguration::new));
}
