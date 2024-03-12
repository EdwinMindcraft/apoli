package io.github.edwinmindcraft.apoli.api.registry;

import io.github.apace100.calio.ClassUtil;
import io.github.edwinmindcraft.apoli.api.power.configuration.*;
import io.github.edwinmindcraft.apoli.common.global.GlobalPowerSet;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class ApoliBuiltinRegistries {
	public static Supplier<IForgeRegistry<ConfiguredPower<?, ?>>> CONFIGURED_POWERS;
    public static Supplier<IForgeRegistry<GlobalPowerSet>> GLOBAL_POWER_SET;

	public static Supplier<IForgeRegistry<ConfiguredBiEntityAction<?, ?>>> CONFIGURED_BIENTITY_ACTIONS;
	public static Supplier<IForgeRegistry<ConfiguredBlockAction<?, ?>>> CONFIGURED_BLOCK_ACTIONS;
	public static Supplier<IForgeRegistry<ConfiguredEntityAction<?, ?>>> CONFIGURED_ENTITY_ACTIONS;
	public static Supplier<IForgeRegistry<ConfiguredItemAction<?, ?>>> CONFIGURED_ITEM_ACTIONS;

	public static Supplier<IForgeRegistry<ConfiguredBiEntityCondition<?, ?>>> CONFIGURED_BIENTITY_CONDITIONS;
	public static Supplier<IForgeRegistry<ConfiguredBiomeCondition<?, ?>>> CONFIGURED_BIOME_CONDITIONS;
	public static Supplier<IForgeRegistry<ConfiguredBlockCondition<?, ?>>> CONFIGURED_BLOCK_CONDITIONS;
	public static Supplier<IForgeRegistry<ConfiguredDamageCondition<?, ?>>> CONFIGURED_DAMAGE_CONDITIONS;
	public static Supplier<IForgeRegistry<ConfiguredEntityCondition<?, ?>>> CONFIGURED_ENTITY_CONDITIONS;
	public static Supplier<IForgeRegistry<ConfiguredFluidCondition<?, ?>>> CONFIGURED_FLUID_CONDITIONS;
	public static Supplier<IForgeRegistry<ConfiguredItemCondition<?, ?>>> CONFIGURED_ITEM_CONDITIONS;

	public static Supplier<IForgeRegistry<ConfiguredModifier<?>>> CONFIGURED_MODIFIERS;
}
