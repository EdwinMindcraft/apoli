package io.github.edwinmindcraft.apoli.api.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.ConfiguredCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.FluidCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;
import net.minecraft.world.level.material.FluidState;

import java.util.function.Supplier;

public final class ConfiguredFluidCondition<C extends IDynamicFeatureConfiguration, F extends FluidCondition<C>> extends ConfiguredCondition<C, F, ConfiguredFluidCondition<?, ?>> {
	public static final Codec<ConfiguredFluidCondition<?, ?>> CODEC = FluidCondition.CODEC.dispatch(ConfiguredFluidCondition::getFactory, FluidCondition::getConditionCodec);
	public static final CodecSet<ConfiguredFluidCondition<?, ?>> CODEC_SET = CalioCodecHelper.forDynamicRegistry(ApoliDynamicRegistries.CONFIGURED_FLUID_CONDITION_KEY, SerializableDataTypes.IDENTIFIER, CODEC);
	public static final Codec<Holder<ConfiguredFluidCondition<?, ?>>> HOLDER = CODEC_SET.holder();

	public static MapCodec<Holder<ConfiguredFluidCondition<?, ?>>> required(String name) {
		return HOLDER.fieldOf(name);
	}

	public static MapCodec<Holder<ConfiguredFluidCondition<?, ?>>> optional(String name) {
		return CalioCodecHelper.registryDefaultedField(HOLDER, name, ApoliDynamicRegistries.CONFIGURED_FLUID_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_FLUID_CONDITIONS);
	}

	public static boolean check(Holder<ConfiguredFluidCondition<?, ?>> condition, FluidState position) {
		return !condition.isBound() || condition.value().check(position);
	}

	public ConfiguredFluidCondition(Supplier<F> factory, C configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(FluidState fluid) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), fluid);
	}

	@Override
	public String toString() {
		return "CFC:" + ApoliRegistries.FLUID_CONDITION.getKey(this.getFactory()) + "(" + this.getData() + ")-" + this.getConfiguration();
	}
}