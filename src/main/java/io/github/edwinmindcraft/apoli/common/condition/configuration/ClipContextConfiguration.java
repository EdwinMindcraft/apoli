package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import net.minecraft.world.level.ClipContext;

public record ClipContextConfiguration(ClipContext.Block block,
									   ClipContext.Fluid fluid) implements IDynamicFeatureConfiguration {
	public static final MapCodec<ClipContextConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			SerializableDataTypes.SHAPE_TYPE.optionalFieldOf("shape_type", ClipContext.Block.VISUAL).forGetter(ClipContextConfiguration::block),
			SerializableDataTypes.FLUID_HANDLING.optionalFieldOf("fluid_handling", ClipContext.Fluid.NONE).forGetter(ClipContextConfiguration::fluid)
	).apply(instance, ClipContextConfiguration::new));
}
