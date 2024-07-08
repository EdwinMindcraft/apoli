package io.github.edwinmindcraft.apoli.api.registry;

import io.github.edwinmindcraft.apoli.api.power.configuration.*;
import net.minecraft.core.Registry;

public class ApoliBuiltinRegistries {
	public static Registry<ConfiguredBiEntityAction<?, ?>> CONFIGURED_BIENTITY_ACTIONS;
	public static Registry<ConfiguredBlockAction<?, ?>> CONFIGURED_BLOCK_ACTIONS;
	public static Registry<ConfiguredEntityAction<?, ?>> CONFIGURED_ENTITY_ACTIONS;
	public static Registry<ConfiguredItemAction<?, ?>> CONFIGURED_ITEM_ACTIONS;

	public static Registry<ConfiguredBiEntityCondition<?, ?>> CONFIGURED_BIENTITY_CONDITIONS;
	public static Registry<ConfiguredBiomeCondition<?, ?>> CONFIGURED_BIOME_CONDITIONS;
	public static Registry<ConfiguredBlockCondition<?, ?>> CONFIGURED_BLOCK_CONDITIONS;
	public static Registry<ConfiguredDamageCondition<?, ?>> CONFIGURED_DAMAGE_CONDITIONS;
	public static Registry<ConfiguredEntityCondition<?, ?>> CONFIGURED_ENTITY_CONDITIONS;
	public static Registry<ConfiguredFluidCondition<?, ?>> CONFIGURED_FLUID_CONDITIONS;
	public static Registry<ConfiguredItemCondition<?, ?>> CONFIGURED_ITEM_CONDITIONS;

	public static Registry<ConfiguredModifier<?>> CONFIGURED_MODIFIERS;
}
