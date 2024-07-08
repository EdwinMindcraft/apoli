package io.github.edwinmindcraft.apoli.common.action.meta;

import com.mojang.serialization.MapCodec;

public record NothingConfiguration<T>() implements IDelegatedActionConfiguration<T> {
	public static <T> MapCodec<NothingConfiguration<T>> codec() {
		return MapCodec.unit(NothingConfiguration::new);
	}

	@Override
	public void execute(T parameters) {}
}
