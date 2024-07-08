package io.github.edwinmindcraft.apoli.common.action.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.configuration.MustBeBound;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public record SideConfiguration<T, V>(Side side, @MustBeBound Holder<T> action, Predicate<V> serverCheck,
                                      BiConsumer<T, V> executor) implements IDelegatedActionConfiguration<V> {
	public static <T, V> MapCodec<SideConfiguration<T, V>> codec(CodecSet<T> codec, Predicate<V> serverCheck, BiConsumer<T, V> executor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				SerializableDataType.enumValue(Side.class).fieldOf("side").forGetter(SideConfiguration::side),
				codec.holder().fieldOf("action").forGetter(SideConfiguration::action)
		).apply(instance, (side, action) -> new SideConfiguration<>(side, action, serverCheck, executor)));
	}

	@Override
	public void execute(V parameters) {
		boolean isServer = serverCheck.test(parameters);
		if (action().isBound() && (this.side() == Side.CLIENT) != isServer)
			executor().accept(action().value(), parameters);
	}

	public enum Side {
		CLIENT, SERVER
	}
}
