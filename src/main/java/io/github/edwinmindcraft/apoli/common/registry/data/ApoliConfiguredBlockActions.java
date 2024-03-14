package io.github.edwinmindcraft.apoli.common.registry.data;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.action.meta.NothingConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.action.ApoliBiEntityActions;
import io.github.edwinmindcraft.apoli.common.registry.action.ApoliBlockActions;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

public class ApoliConfiguredBlockActions {
	public static ResourceKey<ConfiguredBlockAction<?, ?>> NONE = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_BLOCK_ACTION_KEY, Apoli.identifier("none"));

	public static void bootstrap(BootstapContext<ConfiguredBlockAction<?, ?>> context) {
		context.register(NONE, ApoliBlockActions.NOTHING.get().configure(new NothingConfiguration<>()));
	}
}
