package io.github.edwinmindcraft.apoli.common.registry;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.configuration.*;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.global.GlobalPowerSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

public class ApoliDynamicRegisters {

	public static final DeferredRegister<ConfiguredPower<?, ?>> CONFIGURED_POWERS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, Apoli.MODID);
	public static final DeferredRegister<GlobalPowerSet> GLOBAL_POWER_SETS = DeferredRegister.create(ApoliDynamicRegistries.GLOBAL_POWER_SET, Apoli.MODID);

	public static final DeferredRegister<ConfiguredBiEntityAction<?, ?>> CONFIGURED_BIENTITY_ACTIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_BIENTITY_ACTION_KEY, Apoli.MODID);
	public static final DeferredRegister<ConfiguredBlockAction<?, ?>> CONFIGURED_BLOCK_ACTIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_BLOCK_ACTION_KEY, Apoli.MODID);
	public static final DeferredRegister<ConfiguredEntityAction<?, ?>> CONFIGURED_ENTITY_ACTIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_ENTITY_ACTION_KEY, Apoli.MODID);
	public static final DeferredRegister<ConfiguredItemAction<?, ?>> CONFIGURED_ITEM_ACTIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_ITEM_ACTION_KEY, Apoli.MODID);

	public static final DeferredRegister<ConfiguredBiEntityCondition<?, ?>> CONFIGURED_BIENTITY_CONDITIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_BIENTITY_CONDITION_KEY, Apoli.MODID);
	public static final DeferredRegister<ConfiguredBiomeCondition<?, ?>> CONFIGURED_BIOME_CONDITIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_BIOME_CONDITION_KEY, Apoli.MODID);
	public static final DeferredRegister<ConfiguredBlockCondition<?, ?>> CONFIGURED_BLOCK_CONDITIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_BLOCK_CONDITION_KEY, Apoli.MODID);
	public static final DeferredRegister<ConfiguredDamageCondition<?, ?>> CONFIGURED_DAMAGE_CONDITIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_DAMAGE_CONDITION_KEY, Apoli.MODID);
	public static final DeferredRegister<ConfiguredEntityCondition<?, ?>> CONFIGURED_ENTITY_CONDITIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_ENTITY_CONDITION_KEY, Apoli.MODID);
	public static final DeferredRegister<ConfiguredFluidCondition<?, ?>> CONFIGURED_FLUID_CONDITIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_FLUID_CONDITION_KEY, Apoli.MODID);
	public static final DeferredRegister<ConfiguredItemCondition<?, ?>> CONFIGURED_ITEM_CONDITIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_ITEM_CONDITION_KEY, Apoli.MODID);

	public static final DeferredRegister<ConfiguredModifier<?>> CONFIGURED_MODIFIERS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_MODIFIER_KEY, Apoli.MODID);

	public static void initialize(IEventBus bus) {
		ApoliBuiltinRegistries.CONFIGURED_POWERS = CONFIGURED_POWERS.makeRegistry(() -> new RegistryBuilder<ConfiguredPower<?, ?>>().hasTags().disableSaving());
		ApoliBuiltinRegistries.GLOBAL_POWER_SET = GLOBAL_POWER_SETS.makeRegistry(() -> new RegistryBuilder<GlobalPowerSet>().hasTags().disableSaving());

		ApoliBuiltinRegistries.CONFIGURED_BIENTITY_ACTIONS = CONFIGURED_BIENTITY_ACTIONS.makeRegistry(() -> new RegistryBuilder<ConfiguredBiEntityAction<?, ?>>().disableSaving().hasTags().setDefaultKey(ApoliDynamicRegistries.ACTION_DEFAULT));
		ApoliBuiltinRegistries.CONFIGURED_BLOCK_ACTIONS = CONFIGURED_BLOCK_ACTIONS.makeRegistry(() -> new RegistryBuilder<ConfiguredBlockAction<?, ?>>().disableSaving().hasTags().setDefaultKey(ApoliDynamicRegistries.ACTION_DEFAULT));
		ApoliBuiltinRegistries.CONFIGURED_ENTITY_ACTIONS = CONFIGURED_ENTITY_ACTIONS.makeRegistry(() -> new RegistryBuilder<ConfiguredEntityAction<?, ?>>().disableSaving().hasTags().setDefaultKey(ApoliDynamicRegistries.ACTION_DEFAULT));
		ApoliBuiltinRegistries.CONFIGURED_ITEM_ACTIONS = CONFIGURED_ITEM_ACTIONS.makeRegistry(() -> new RegistryBuilder<ConfiguredItemAction<?, ?>>().disableSaving().hasTags().setDefaultKey(ApoliDynamicRegistries.ACTION_DEFAULT));

		ApoliBuiltinRegistries.CONFIGURED_BIENTITY_CONDITIONS = CONFIGURED_BIENTITY_CONDITIONS.makeRegistry(() -> new RegistryBuilder<ConfiguredBiEntityCondition<?, ?>>().disableSaving().hasTags().setDefaultKey(ApoliDynamicRegistries.CONDITION_DEFAULT));
		ApoliBuiltinRegistries.CONFIGURED_BIOME_CONDITIONS = CONFIGURED_BIOME_CONDITIONS.makeRegistry(() -> new RegistryBuilder<ConfiguredBiomeCondition<?, ?>>().disableSaving().hasTags().setDefaultKey(ApoliDynamicRegistries.CONDITION_DEFAULT));
		ApoliBuiltinRegistries.CONFIGURED_BLOCK_CONDITIONS = CONFIGURED_BLOCK_CONDITIONS.makeRegistry(() -> new RegistryBuilder<ConfiguredBlockCondition<?, ?>>().disableSaving().hasTags().setDefaultKey(ApoliDynamicRegistries.CONDITION_DEFAULT));
		ApoliBuiltinRegistries.CONFIGURED_DAMAGE_CONDITIONS = CONFIGURED_DAMAGE_CONDITIONS.makeRegistry(() -> new RegistryBuilder<ConfiguredDamageCondition<?, ?>>().disableSaving().hasTags().setDefaultKey(ApoliDynamicRegistries.CONDITION_DEFAULT));
		ApoliBuiltinRegistries.CONFIGURED_ENTITY_CONDITIONS = CONFIGURED_ENTITY_CONDITIONS.makeRegistry(() -> new RegistryBuilder<ConfiguredEntityCondition<?, ?>>().disableSaving().hasTags().setDefaultKey(ApoliDynamicRegistries.CONDITION_DEFAULT));
		ApoliBuiltinRegistries.CONFIGURED_FLUID_CONDITIONS = CONFIGURED_FLUID_CONDITIONS.makeRegistry(() -> new RegistryBuilder<ConfiguredFluidCondition<?, ?>>().disableSaving().hasTags().setDefaultKey(ApoliDynamicRegistries.CONDITION_DEFAULT));
		ApoliBuiltinRegistries.CONFIGURED_ITEM_CONDITIONS = CONFIGURED_ITEM_CONDITIONS.makeRegistry(() -> new RegistryBuilder<ConfiguredItemCondition<?, ?>>().disableSaving().hasTags().setDefaultKey(ApoliDynamicRegistries.CONDITION_DEFAULT));

		ApoliBuiltinRegistries.CONFIGURED_MODIFIERS = CONFIGURED_MODIFIERS.makeRegistry(() -> new RegistryBuilder<ConfiguredModifier<?>>().disableSaving().hasTags());

		CONFIGURED_POWERS.register(bus);
		GLOBAL_POWER_SETS.register(bus);

		CONFIGURED_BIENTITY_ACTIONS.register(bus);
		CONFIGURED_BLOCK_ACTIONS.register(bus);
		CONFIGURED_ENTITY_ACTIONS.register(bus);
		CONFIGURED_ITEM_ACTIONS.register(bus);

		CONFIGURED_BIENTITY_CONDITIONS.register(bus);
		CONFIGURED_BIOME_CONDITIONS.register(bus);
		CONFIGURED_BLOCK_CONDITIONS.register(bus);
		CONFIGURED_DAMAGE_CONDITIONS.register(bus);
		CONFIGURED_ENTITY_CONDITIONS.register(bus);
		CONFIGURED_FLUID_CONDITIONS.register(bus);
		CONFIGURED_ITEM_CONDITIONS.register(bus);

		CONFIGURED_MODIFIERS.register(bus);
		bus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
			event.dataPackRegistry(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, ConfiguredPower.DIRECT_CODEC, ConfiguredPower.DIRECT_CODEC);
			event.dataPackRegistry(ApoliDynamicRegistries.GLOBAL_POWER_SET, GlobalPowerSet.DIRECT_CODEC, GlobalPowerSet.DIRECT_CODEC);
			event.dataPackRegistry(ApoliDynamicRegistries.CONFIGURED_BIENTITY_ACTION_KEY, ConfiguredBiEntityAction.DIRECT_CODEC, ConfiguredBiEntityAction.DIRECT_CODEC);
			event.dataPackRegistry(ApoliDynamicRegistries.CONFIGURED_BLOCK_ACTION_KEY, ConfiguredBlockAction.CODEC, ConfiguredBlockAction.CODEC);
			event.dataPackRegistry(ApoliDynamicRegistries.CONFIGURED_ENTITY_ACTION_KEY, ConfiguredEntityAction.CODEC, ConfiguredEntityAction.CODEC);
			event.dataPackRegistry(ApoliDynamicRegistries.CONFIGURED_ITEM_ACTION_KEY, ConfiguredItemAction.CODEC, ConfiguredItemAction.CODEC);

			event.dataPackRegistry(ApoliDynamicRegistries.CONFIGURED_BIENTITY_CONDITION_KEY, ConfiguredBiEntityCondition.CODEC, ConfiguredBiEntityCondition.CODEC);
			event.dataPackRegistry(ApoliDynamicRegistries.CONFIGURED_BIOME_CONDITION_KEY, ConfiguredBiomeCondition.CODEC, ConfiguredBiomeCondition.CODEC);
			event.dataPackRegistry(ApoliDynamicRegistries.CONFIGURED_BLOCK_CONDITION_KEY, ConfiguredBlockCondition.CODEC, ConfiguredBlockCondition.CODEC);
			event.dataPackRegistry(ApoliDynamicRegistries.CONFIGURED_DAMAGE_CONDITION_KEY, ConfiguredDamageCondition.CODEC, ConfiguredDamageCondition.CODEC);
			event.dataPackRegistry(ApoliDynamicRegistries.CONFIGURED_ENTITY_CONDITION_KEY, ConfiguredEntityCondition.CODEC, ConfiguredEntityCondition.CODEC);
			event.dataPackRegistry(ApoliDynamicRegistries.CONFIGURED_FLUID_CONDITION_KEY, ConfiguredFluidCondition.CODEC, ConfiguredFluidCondition.CODEC);
			event.dataPackRegistry(ApoliDynamicRegistries.CONFIGURED_ITEM_CONDITION_KEY, ConfiguredItemCondition.CODEC, ConfiguredItemCondition.CODEC);

			event.dataPackRegistry(ApoliDynamicRegistries.CONFIGURED_MODIFIER_KEY, ConfiguredModifier.CODEC, ConfiguredModifier.CODEC);
		});
	}
}
