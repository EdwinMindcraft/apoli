package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.DoubleComparisonConfiguration;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public record FluidTagComparisonConfiguration(DoubleComparisonConfiguration comparison,
											  TagKey<Fluid> tag) implements IDynamicFeatureConfiguration {
	public static final MapCodec<FluidTagComparisonConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			DoubleComparisonConfiguration.CODEC.forGetter(FluidTagComparisonConfiguration::comparison),
			SerializableDataTypes.FLUID_TAG.fieldOf("fluid").forGetter(FluidTagComparisonConfiguration::tag)
	).apply(instance, FluidTagComparisonConfiguration::new));
}
