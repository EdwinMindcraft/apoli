package io.github.edwinmindcraft.apoli.api.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.ConfiguredCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

public final class ConfiguredEntityCondition<C extends IDynamicFeatureConfiguration, F extends EntityCondition<C>> extends ConfiguredCondition<C, F, ConfiguredEntityCondition<?, ?>> {
	public static final Codec<ConfiguredEntityCondition<?, ?>> CODEC = EntityCondition.CODEC.dispatch(ConfiguredEntityCondition::getFactory, EntityCondition::getConditionCodec);
	public static final CodecSet<ConfiguredEntityCondition<?, ?>> CODEC_SET = CalioCodecHelper.forDynamicRegistry(ApoliDynamicRegistries.CONFIGURED_ENTITY_CONDITION_KEY, SerializableDataTypes.IDENTIFIER, CODEC);
	public static final Codec<Holder<ConfiguredEntityCondition<?, ?>>> HOLDER = CODEC_SET.holder();

	public static MapCodec<Holder<ConfiguredEntityCondition<?, ?>>> required(String name) {
		return HOLDER.fieldOf(name);
	}

	public static MapCodec<Holder<ConfiguredEntityCondition<?, ?>>> optional(String name) {
		return CalioCodecHelper.registryDefaultedField(HOLDER, name, ApoliDynamicRegistries.CONFIGURED_ENTITY_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_ENTITY_CONDITIONS);
	}

	public static boolean check(Holder<ConfiguredEntityCondition<?, ?>> condition, Entity entity) {
		return !condition.isBound() || condition.value().check(entity);
	}

	public ConfiguredEntityCondition(Supplier<F> factory, C configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(Entity entity) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), entity);
	}

	@Override
	public String toString() {
		return "CEC:" + ApoliRegistries.ENTITY_CONDITION.getKey(this.getFactory()) + "(" + this.getData() + ")-" + this.getConfiguration();
	}
}