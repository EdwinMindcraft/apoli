package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.apoli.util.Shape;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.IntegerComparisonConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;

public record BlockInRadiusConfiguration(
		Holder<ConfiguredBlockCondition<?, ?>> blockCondition, int radius,
		Shape shape, IntegerComparisonConfiguration comparison) implements IDynamicFeatureConfiguration {

	public static final MapCodec<BlockInRadiusConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ConfiguredBlockCondition.required("block_condition").forGetter(BlockInRadiusConfiguration::blockCondition),
			CalioCodecHelper.INT.fieldOf("radius").forGetter(BlockInRadiusConfiguration::radius),
			SerializableDataType.enumValue(Shape.class).optionalFieldOf("shape", Shape.CUBE).forGetter(BlockInRadiusConfiguration::shape),
			IntegerComparisonConfiguration.withDefaults(Comparison.GREATER_THAN_OR_EQUAL, 1).forGetter(BlockInRadiusConfiguration::comparison)
	).apply(instance, BlockInRadiusConfiguration::new));

	public BlockInRadiusConfiguration inverse() {
		return new BlockInRadiusConfiguration(this.blockCondition(), this.radius(), this.shape(), this.comparison().inverse());
	}
}
