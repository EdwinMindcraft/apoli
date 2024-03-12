package io.github.edwinmindcraft.apoli.api.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConfiguredFactory;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

public final class ConfiguredEntityAction<C extends IDynamicFeatureConfiguration, F extends EntityAction<C>> extends ConfiguredFactory<C, F, ConfiguredEntityAction<?, ?>> {
	public static final Codec<ConfiguredEntityAction<?, ?>> CODEC = EntityAction.CODEC.dispatch(ConfiguredEntityAction::getFactory, EntityAction::getCodec);

	public static final CodecSet<ConfiguredEntityAction<?, ?>> CODEC_SET = CalioCodecHelper.forDynamicRegistry(ApoliDynamicRegistries.CONFIGURED_ENTITY_ACTION_KEY, SerializableDataTypes.IDENTIFIER, CODEC);
	public static final Codec<Holder<ConfiguredEntityAction<?, ?>>> HOLDER = CODEC_SET.holder();

	public static MapCodec<Holder<ConfiguredEntityAction<?, ?>>> required(String name) {
		return HOLDER.fieldOf(name);
	}

	public static MapCodec<Holder<ConfiguredEntityAction<?, ?>>> optional(String name) {
		return CalioCodecHelper.registryDefaultedField(HOLDER, name, ApoliDynamicRegistries.CONFIGURED_ENTITY_ACTION_KEY, ApoliBuiltinRegistries.CONFIGURED_ENTITY_ACTIONS);
	}

	public static void execute(Holder<ConfiguredEntityAction<?, ?>> action, Entity entity) {
		if (action.isBound())
			action.value().execute(entity);
	}

	public ConfiguredEntityAction(Supplier<F> factory, C configuration) {
		super(factory, configuration);
	}

	public void execute(Entity entity) {
		this.getFactory().execute(this.getConfiguration(), entity);
	}

	@Override
	public String toString() {
		return "CEA:" + ApoliRegistries.ENTITY_ACTION.getKey(this.getFactory()) + "-" + this.getConfiguration();
	}
}
