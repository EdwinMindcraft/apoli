package io.github.edwinmindcraft.apoli.common.registry.data;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.action.meta.NothingConfiguration;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConstantConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.action.ApoliBiEntityActions;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliBiEntityConditions;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

public class ApoliConfiguredBiEntityActions {
	public static ResourceKey<ConfiguredBiEntityAction<?, ?>> NONE = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_BIENTITY_ACTION_KEY, Apoli.identifier("none"));

	public static void bootstrap(BootstapContext<ConfiguredBiEntityAction<?, ?>> context) {
		context.register(NONE, ApoliBiEntityActions.NOTHING.get().configure(new NothingConfiguration<>()));
	}
}
