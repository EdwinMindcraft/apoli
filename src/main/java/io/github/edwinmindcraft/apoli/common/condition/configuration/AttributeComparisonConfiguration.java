package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.DoubleComparisonConfiguration;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;

public record AttributeComparisonConfiguration(Holder<Attribute> attribute,
											   DoubleComparisonConfiguration comparison) implements IDynamicFeatureConfiguration {
	public static final MapCodec<AttributeComparisonConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			SerializableDataTypes.ATTRIBUTE_ENTRY.fieldOf("attribute").forGetter(AttributeComparisonConfiguration::attribute),
			DoubleComparisonConfiguration.MAP_CODEC.forGetter(AttributeComparisonConfiguration::comparison)
	).apply(instance, AttributeComparisonConfiguration::new));
}
