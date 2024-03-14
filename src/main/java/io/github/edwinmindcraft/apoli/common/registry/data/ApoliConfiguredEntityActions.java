package io.github.edwinmindcraft.apoli.common.registry.data;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.action.meta.NothingConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.action.ApoliBiEntityActions;
import io.github.edwinmindcraft.apoli.common.registry.action.ApoliEntityActions;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

public class ApoliConfiguredEntityActions {
	public static ResourceKey<ConfiguredEntityAction<?, ?>> NONE = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_ENTITY_ACTION_KEY, Apoli.identifier("none"));

	public static void bootstrap(BootstapContext<ConfiguredEntityAction<?, ?>> context) {
		context.register(NONE, ApoliEntityActions.NOTHING.get().configure(new NothingConfiguration<>()));
	}
}
