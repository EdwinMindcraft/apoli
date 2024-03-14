package io.github.edwinmindcraft.apoli.data;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.registry.data.*;
import net.minecraft.core.RegistrySetBuilder;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ApoliDataGenerator {
	public static @NotNull RegistrySetBuilder createData() {
		RegistrySetBuilder builder = new RegistrySetBuilder();
		builder.add(ApoliDynamicRegistries.CONFIGURED_BIENTITY_ACTION_KEY, ApoliConfiguredBiEntityActions::bootstrap);
		builder.add(ApoliDynamicRegistries.CONFIGURED_BLOCK_ACTION_KEY, ApoliConfiguredBlockActions::bootstrap);
		builder.add(ApoliDynamicRegistries.CONFIGURED_ENTITY_ACTION_KEY, ApoliConfiguredEntityActions::bootstrap);
		builder.add(ApoliDynamicRegistries.CONFIGURED_ITEM_ACTION_KEY, ApoliConfiguredItemActions::bootstrap);

		builder.add(ApoliDynamicRegistries.CONFIGURED_BIENTITY_CONDITION_KEY, ApoliConfiguredBiEntityConditions::bootstrap);
		builder.add(ApoliDynamicRegistries.CONFIGURED_BIOME_CONDITION_KEY, ApoliConfiguredBiomeConditions::bootstrap);
		builder.add(ApoliDynamicRegistries.CONFIGURED_BLOCK_CONDITION_KEY, ApoliConfiguredBlockConditions::bootstrap);
		builder.add(ApoliDynamicRegistries.CONFIGURED_DAMAGE_CONDITION_KEY, ApoliConfiguredDamageConditions::bootstrap);
		builder.add(ApoliDynamicRegistries.CONFIGURED_ENTITY_CONDITION_KEY, ApoliConfiguredEntityConditions::bootstrap);
		builder.add(ApoliDynamicRegistries.CONFIGURED_FLUID_CONDITION_KEY, ApoliConfiguredFluidConditions::bootstrap);
		builder.add(ApoliDynamicRegistries.CONFIGURED_ITEM_CONDITION_KEY, ApoliConfiguredItemConditions::bootstrap);
		return builder;
	}

	public static void gatherData(GatherDataEvent event) {
		event.getGenerator().<DatapackBuiltinEntriesProvider>addProvider(event.includeServer(), output -> new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), createData(), Set.of(Apoli.MODID)));
	}
}
