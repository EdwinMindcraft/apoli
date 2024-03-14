package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;

public record OffsetConfiguration<T>(Holder<T> value, int x, int y, int z) implements IDynamicFeatureConfiguration {

	public static <T> Codec<OffsetConfiguration<T>> codec(MapCodec<Holder<T>> codec) {
		return RecordCodecBuilder.create(instance -> instance.group(
				codec.forGetter(OffsetConfiguration::value),
				ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "x", 0).forGetter(OffsetConfiguration::x),
				ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "y", 0).forGetter(OffsetConfiguration::y),
				ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "z", 0).forGetter(OffsetConfiguration::z)
		).apply(instance, OffsetConfiguration::new));
	}

	public BlockPos asBlockPos() {
		return new BlockPos(this.x, this.y, this.z);
	}
}
