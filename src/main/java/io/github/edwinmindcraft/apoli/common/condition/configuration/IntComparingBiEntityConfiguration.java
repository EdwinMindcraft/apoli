package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.util.Comparison;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.IntegerComparisonConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import net.minecraft.core.Holder;

public record IntComparingBiEntityConfiguration(IntegerComparisonConfiguration comparison,
												Holder<ConfiguredBiEntityCondition<?, ?>> biEntityCondition) implements IDynamicFeatureConfiguration {
	public static final MapCodec<IntComparingBiEntityConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			IntegerComparisonConfiguration.withDefaults(Comparison.GREATER_THAN_OR_EQUAL, 1).forGetter(IntComparingBiEntityConfiguration::comparison),
			ConfiguredBiEntityCondition.optional("bientity_condition").forGetter(IntComparingBiEntityConfiguration::biEntityCondition)
	).apply(instance, IntComparingBiEntityConfiguration::new));
}
