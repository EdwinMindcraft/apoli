package io.github.edwinmindcraft.apoli.api.power.factory;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.IConditionFactory;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredFluidCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.world.level.material.FluidState;

public abstract class FluidCondition<T extends IDynamicFeatureConfiguration> implements IConditionFactory<T, ConfiguredFluidCondition<T, ?>, FluidCondition<T>> {
	public static final Codec<FluidCondition<?>> CODEC = ApoliRegistries.FLUID_CONDITION.byNameCodec();
	private final Codec<ConfiguredFluidCondition<T, ?>> codec;

	protected FluidCondition(Codec<T> codec) {
		this.codec = IConditionFactory.conditionCodec(codec, this);
	}

	@Override
	public Codec<ConfiguredFluidCondition<T, ?>> getConditionCodec() {
		return this.codec;
	}

	@Override
	public final ConfiguredFluidCondition<T, ?> configure(T input, ConditionData data) {
		return new ConfiguredFluidCondition<>(() -> this, input, data);
	}

	public boolean check(T configuration, FluidState fluid) {
		return false;
	}

	public boolean check(T configuration, ConditionData data, FluidState fluid) {
		return data.inverted() ^ this.check(configuration, fluid);
	}
}
