package io.github.edwinmindcraft.apoli.api.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConfiguredFactory;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

public final class ConfiguredBiEntityAction<C extends IDynamicFeatureConfiguration, F extends BiEntityAction<C>> extends ConfiguredFactory<C, F, ConfiguredBiEntityAction<?, ?>> {
	public static final Codec<ConfiguredBiEntityAction<?, ?>> DIRECT_CODEC = BiEntityAction.CODEC.dispatch(ConfiguredBiEntityAction::getFactory, BiEntityAction::getCodec);
	public static final Codec<Holder<ConfiguredBiEntityAction<?, ?>>> REFERENCE_CODEC = RegistryFixedCodec.create(ApoliDynamicRegistries.CONFIGURED_BIENTITY_ACTION_KEY);
	public static final Codec<Holder<ConfiguredBiEntityAction<?, ?>>> CODEC = RegistryFileCodec.create(ApoliDynamicRegistries.CONFIGURED_BIENTITY_ACTION_KEY, DIRECT_CODEC);
	public static final Codec<HolderSet<ConfiguredBiEntityAction<?, ?>>> REFERENCE_SET_CODEC = RegistryCodecs.homogeneousList(ApoliDynamicRegistries.CONFIGURED_BIENTITY_ACTION_KEY);


	public static MapCodec<Holder<ConfiguredBiEntityAction<?, ?>>> required(String name) {
		return CODEC.fieldOf(name);
	}

	// FIXME: This...
	public static MapCodec<Holder<ConfiguredBiEntityAction<?, ?>>> optional(String name) {
		return CODEC.optionalFieldOf(name).xmap(optional -> optional.orElseGet(() -> ));
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
