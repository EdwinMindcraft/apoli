package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public record ConditionedAttributeConfiguration(AttributeConfiguration attributes,
												int tickRate) implements IDynamicFeatureConfiguration {
	public static final Codec<ConditionedAttributeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			AttributeConfiguration.MAP_CODEC.forGetter(ConditionedAttributeConfiguration::attributes),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "tick_rate", 20).forGetter(ConditionedAttributeConfiguration::tickRate)
	).apply(instance, ConditionedAttributeConfiguration::new));
}
