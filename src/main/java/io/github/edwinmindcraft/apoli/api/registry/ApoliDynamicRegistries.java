package io.github.edwinmindcraft.apoli.api.registry;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.power.configuration.*;
import io.github.edwinmindcraft.apoli.common.global.GlobalPowerSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ApoliDynamicRegistries {
	public static final ResourceKey<Registry<ConfiguredPower<?, ?>>> CONFIGURED_POWER_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("configured_power"));
    public static final ResourceKey<Registry<GlobalPowerSet>> GLOBAL_POWER_SET = ResourceKey.createRegistryKey(ApoliAPI.identifier("global_power_set"));

	public static final ResourceKey<Registry<ConfiguredBiEntityAction<?, ?>>> CONFIGURED_BIENTITY_ACTION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("configured_bientity_action"));
	public static final ResourceKey<Registry<ConfiguredBlockAction<?, ?>>> CONFIGURED_BLOCK_ACTION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("configured_block_action"));
	public static final ResourceKey<Registry<ConfiguredEntityAction<?, ?>>> CONFIGURED_ENTITY_ACTION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("configured_entity_action"));
	public static final ResourceKey<Registry<ConfiguredItemAction<?, ?>>> CONFIGURED_ITEM_ACTION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("configured_item_action"));

	public static final ResourceKey<Registry<ConfiguredBiEntityCondition<?, ?>>> CONFIGURED_BIENTITY_CONDITION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("configured_bientity_condition"));
	public static final ResourceKey<Registry<ConfiguredBiomeCondition<?, ?>>> CONFIGURED_BIOME_CONDITION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("configured_biome_condition"));
	public static final ResourceKey<Registry<ConfiguredBlockCondition<?, ?>>> CONFIGURED_BLOCK_CONDITION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("configured_block_condition"));
	public static final ResourceKey<Registry<ConfiguredDamageCondition<?, ?>>> CONFIGURED_DAMAGE_CONDITION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("configured_damage_condition"));
	public static final ResourceKey<Registry<ConfiguredEntityCondition<?, ?>>> CONFIGURED_ENTITY_CONDITION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("configured_entity_condition"));
	public static final ResourceKey<Registry<ConfiguredFluidCondition<?, ?>>> CONFIGURED_FLUID_CONDITION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("configured_fluid_condition"));
	public static final ResourceKey<Registry<ConfiguredItemCondition<?, ?>>> CONFIGURED_ITEM_CONDITION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("configured_item_condition"));

	public static final ResourceKey<Registry<ConfiguredModifier<?>>> CONFIGURED_MODIFIER_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("configured_modifier"));

	public static final ResourceLocation ACTION_DEFAULT = Apoli.identifier("nothing");
	public static final ResourceLocation CONDITION_DEFAULT = Apoli.identifier("allow");
	public static final ResourceLocation DENY = Apoli.identifier("deny");
}
