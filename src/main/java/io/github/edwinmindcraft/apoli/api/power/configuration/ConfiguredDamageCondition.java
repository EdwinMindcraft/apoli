package io.github.edwinmindcraft.apoli.api.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.ConfiguredCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.DamageCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;

import java.util.function.Supplier;

public final class ConfiguredDamageCondition<C extends IDynamicFeatureConfiguration, F extends DamageCondition<C>> extends ConfiguredCondition<C, F, ConfiguredDamageCondition<?, ?>> {
	public static final Codec<ConfiguredDamageCondition<?, ?>> CODEC = DamageCondition.CODEC.dispatch(ConfiguredDamageCondition::getFactory, DamageCondition::getConditionCodec);
	public static final CodecSet<ConfiguredDamageCondition<?, ?>> CODEC_SET = CalioCodecHelper.forDynamicRegistry(ApoliDynamicRegistries.CONFIGURED_DAMAGE_CONDITION_KEY, SerializableDataTypes.IDENTIFIER, CODEC);
	public static final Codec<Holder<ConfiguredDamageCondition<?, ?>>> HOLDER = CODEC_SET.holder();

	public static MapCodec<Holder<ConfiguredDamageCondition<?, ?>>> required(String name) {
		return HOLDER.fieldOf(name);
	}

	public static MapCodec<Holder<ConfiguredDamageCondition<?, ?>>> optional(String name) {
		return CalioCodecHelper.registryDefaultedField(HOLDER, name, ApoliDynamicRegistries.CONFIGURED_DAMAGE_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_DAMAGE_CONDITIONS);
	}

	public static boolean check(Holder<ConfiguredDamageCondition<?, ?>> condition, DamageSource damageSource, float amount) {
		return !condition.isBound() || condition.value().check(damageSource, amount);
	}

	public ConfiguredDamageCondition(Supplier<F> factory, C configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(DamageSource source, float amount) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), source, amount);
	}

	@Override
	public String toString() {
		return "CDC:" + ApoliRegistries.DAMAGE_CONDITION.getKey(this.getFactory()) + "(" + this.getData() + ")-" + this.getConfiguration();
	}
}