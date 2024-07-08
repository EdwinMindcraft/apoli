package io.github.edwinmindcraft.apoli.common.condition.meta;

import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public record ConstantConfiguration<T>(boolean value) implements IDelegatedConditionConfiguration<T> {

	public static <T> MapCodec<ConstantConfiguration<T>> codec() {
		return CalioCodecHelper.BOOL.fieldOf("value").xmap(ConstantConfiguration<T>::new, ConstantConfiguration::value);
	}

	@Override
	public boolean check(T parameters) {
		return this.value;
	}
}
