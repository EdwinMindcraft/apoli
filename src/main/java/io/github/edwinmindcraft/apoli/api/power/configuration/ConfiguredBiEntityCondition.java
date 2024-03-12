package io.github.edwinmindcraft.apoli.api.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.ConfiguredCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

public final class ConfiguredBiEntityCondition<C extends IDynamicFeatureConfiguration, F extends BiEntityCondition<C>> extends ConfiguredCondition<C, F, ConfiguredBiEntityCondition<?, ?>> {
	public static final Codec<ConfiguredBiEntityCondition<?, ?>> CODEC = BiEntityCondition.CODEC.dispatch(ConfiguredBiEntityCondition::getFactory, BiEntityCondition::getConditionCodec);
	public static final CodecSet<ConfiguredBiEntityCondition<?, ?>> CODEC_SET = CalioCodecHelper.forDynamicRegistry(ApoliDynamicRegistries.CONFIGURED_BIENTITY_CONDITION_KEY, SerializableDataTypes.IDENTIFIER, CODEC);
	public static final Codec<Holder<ConfiguredBiEntityCondition<?, ?>>> HOLDER = CODEC_SET.holder();

	public static MapCodec<Holder<ConfiguredBiEntityCondition<?, ?>>> required(String name) {
		return HOLDER.fieldOf(name);
	}

	public static MapCodec<Holder<ConfiguredBiEntityCondition<?, ?>>> optional(String name) {
		return CalioCodecHelper.registryDefaultedField(HOLDER, name, ApoliDynamicRegistries.CONFIGURED_BIENTITY_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_BIENTITY_CONDITIONS);
	}

	public static boolean check(Holder<ConfiguredBiEntityCondition<?, ?>> condition, Entity actor, Entity target) {
		return !condition.isBound() || condition.value().check(actor, target);
	}

	public ConfiguredBiEntityCondition(Supplier<F> factory, C configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(Entity actor, Entity target) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), actor, target);
	}

	@Override
	public String toString() {
		return "CBEC:" + ApoliRegistries.BIENTITY_CONDITION.getKey(this.getFactory()) + "(" + this.getData() + ")-" + this.getConfiguration();
	}
}