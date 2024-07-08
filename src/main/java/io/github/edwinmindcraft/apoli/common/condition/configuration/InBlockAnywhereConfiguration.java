package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.util.Comparison;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.IntegerComparisonConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import net.minecraft.core.Holder;

public record InBlockAnywhereConfiguration(
		Holder<ConfiguredBlockCondition<?, ?>> blockCondition,
		IntegerComparisonConfiguration comparison) implements IDynamicFeatureConfiguration {

	public static final MapCodec<InBlockAnywhereConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ConfiguredBlockCondition.required("block_condition").forGetter(InBlockAnywhereConfiguration::blockCondition),
			IntegerComparisonConfiguration.withDefaults(Comparison.GREATER_THAN_OR_EQUAL, 1).forGetter(InBlockAnywhereConfiguration::comparison)
	).apply(instance, InBlockAnywhereConfiguration::new));

	public InBlockAnywhereConfiguration(ConfiguredBlockCondition<?, ?> condition) {
		this(Holder.direct(condition), new IntegerComparisonConfiguration(Comparison.GREATER_THAN_OR_EQUAL, 1));
	}
}
