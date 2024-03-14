package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.world.level.ClipContext;

public record ClipContextConfiguration(ClipContext.Block block,
									   ClipContext.Fluid fluid) implements IDynamicFeatureConfiguration {
	public static final Codec<ClipContextConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(SerializableDataTypes.SHAPE_TYPE, "shape_type", ClipContext.Block.VISUAL).forGetter(ClipContextConfiguration::block),
			ExtraCodecs.strictOptionalField(SerializableDataTypes.FLUID_HANDLING, "fluid_handling", ClipContext.Fluid.NONE).forGetter(ClipContextConfiguration::fluid)
	).apply(instance, ClipContextConfiguration::new));
}
