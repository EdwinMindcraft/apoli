package io.github.edwinmindcraft.apoli.api.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConfiguredFactory;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

public final class ConfiguredBiEntityAction<C extends IDynamicFeatureConfiguration, F extends BiEntityAction<C>> extends ConfiguredFactory<C, F, ConfiguredBiEntityAction<?, ?>> {
	public static final Codec<ConfiguredBiEntityAction<?, ?>> CODEC = BiEntityAction.CODEC.dispatch(ConfiguredBiEntityAction::getFactory, BiEntityAction::getCodec);
	public static final CodecSet<ConfiguredBiEntityAction<?, ?>> CODEC_SET = CalioCodecHelper.forDynamicRegistry(ApoliDynamicRegistries.CONFIGURED_BIENTITY_ACTION_KEY, SerializableDataTypes.IDENTIFIER, CODEC);
	public static final Codec<Holder<ConfiguredBiEntityAction<?, ?>>> HOLDER = CODEC_SET.holder();

	public static MapCodec<Holder<ConfiguredBiEntityAction<?, ?>>> required(String name) {
		return HOLDER.fieldOf(name);
	}

	public static MapCodec<Holder<ConfiguredBiEntityAction<?, ?>>> optional(String name) {
		return CalioCodecHelper.registryDefaultedField(HOLDER, name, ApoliDynamicRegistries.CONFIGURED_BIENTITY_ACTION_KEY, ApoliBuiltinRegistries.CONFIGURED_BIENTITY_ACTIONS);
	}

	public static void execute(Holder<ConfiguredBiEntityAction<?, ?>> action, Entity actor, Entity target) {
		if (action.isBound())
			action.value().execute(actor, target);
	}

	public ConfiguredBiEntityAction(Supplier<F> factory, C configuration) {
		super(factory, configuration);
	}

	public void execute(Entity actor, Entity target) {
		this.getFactory().execute(this.getConfiguration(), actor, target);
	}

	@Override
	public String toString() {
		return "CBEA:" + ApoliRegistries.BIENTITY_ACTION.getKey(this.getFactory()) + "-" + this.getConfiguration();
	}
}
