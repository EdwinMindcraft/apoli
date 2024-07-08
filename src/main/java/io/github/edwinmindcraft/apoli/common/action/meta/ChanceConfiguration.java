package io.github.edwinmindcraft.apoli.common.action.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.configuration.MustBeBound;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;

import java.util.Random;
import java.util.function.BiConsumer;

public record ChanceConfiguration<T, V>(float chance, @MustBeBound Holder<T> action,
										BiConsumer<T, V> executor) implements IDelegatedActionConfiguration<V> {
	public static <T, V> MapCodec<ChanceConfiguration<T, V>> codec(CodecSet<T> codec, BiConsumer<T, V> executor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				CalioCodecHelper.FLOAT.fieldOf("chance").forGetter(ChanceConfiguration::chance),
				codec.holder().fieldOf("action").forGetter(ChanceConfiguration::action)
		).apply(instance, (chance, action) -> new ChanceConfiguration<>(chance, action, executor)));
	}

	@Override
	public void execute(V parameters) {
		if (new Random().nextFloat() < this.chance())
			this.executor().accept(this.action().value(), parameters);
	}
}
