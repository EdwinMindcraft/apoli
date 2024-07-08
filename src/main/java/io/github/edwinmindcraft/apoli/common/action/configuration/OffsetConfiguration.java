package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;

public record OffsetConfiguration<T>(Holder<T> value, int x, int y, int z) implements IDynamicFeatureConfiguration {

	public static <T> MapCodec<OffsetConfiguration<T>> codec(MapCodec<Holder<T>> codec) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				codec.forGetter(OffsetConfiguration::value),
				CalioCodecHelper.INT.optionalFieldOf("x", 0).forGetter(OffsetConfiguration::x),
				CalioCodecHelper.INT.optionalFieldOf("y", 0).forGetter(OffsetConfiguration::y),
				CalioCodecHelper.INT.optionalFieldOf("z", 0).forGetter(OffsetConfiguration::z)
		).apply(instance, OffsetConfiguration::new));
	}

	public BlockPos asBlockPos() {
		return new BlockPos(this.x, this.y, this.z);
	}
}
