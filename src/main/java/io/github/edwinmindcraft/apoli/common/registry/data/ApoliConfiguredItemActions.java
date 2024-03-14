package io.github.edwinmindcraft.apoli.common.registry.data;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.action.meta.NothingConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.action.ApoliEntityActions;
import io.github.edwinmindcraft.apoli.common.registry.action.ApoliItemActions;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

public class ApoliConfiguredItemActions {
	public static ResourceKey<ConfiguredItemAction<?, ?>> NONE = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_ITEM_ACTION_KEY, Apoli.identifier("none"));

	public static void bootstrap(BootstapContext<ConfiguredItemAction<?, ?>> context) {
		context.register(NONE, ApoliItemActions.NOTHING.get().configure(new NothingConfiguration<>()));
	}
}
