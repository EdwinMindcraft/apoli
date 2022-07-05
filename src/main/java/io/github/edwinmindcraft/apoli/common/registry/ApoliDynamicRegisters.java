package io.github.edwinmindcraft.apoli.common.registry;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.configuration.*;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;

public class ApoliDynamicRegisters {

	public static final DeferredRegister<ConfiguredPower<?, ?>> CONFIGURED_POWERS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_POWER_KEY.location(), Apoli.MODID);

	public static final DeferredRegister<ConfiguredBiEntityAction<?, ?>> CONFIGURED_BIENTITY_ACTIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_BIENTITY_ACTION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<ConfiguredBlockAction<?, ?>> CONFIGURED_BLOCK_ACTIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_BLOCK_ACTION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<ConfiguredEntityAction<?, ?>> CONFIGURED_ENTITY_ACTIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_ENTITY_ACTION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<ConfiguredItemAction<?, ?>> CONFIGURED_ITEM_ACTIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_ITEM_ACTION_KEY.location(), Apoli.MODID);

	public static final DeferredRegister<ConfiguredBiEntityCondition<?, ?>> CONFIGURED_BIENTITY_CONDITIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_BIENTITY_CONDITION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<ConfiguredBiomeCondition<?, ?>> CONFIGURED_BIOME_CONDITIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_BIOME_CONDITION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<ConfiguredBlockCondition<?, ?>> CONFIGURED_BLOCK_CONDITIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_BLOCK_CONDITION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<ConfiguredDamageCondition<?, ?>> CONFIGURED_DAMAGE_CONDITIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_DAMAGE_CONDITION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<ConfiguredEntityCondition<?, ?>> CONFIGURED_ENTITY_CONDITIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_ENTITY_CONDITION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<ConfiguredFluidCondition<?, ?>> CONFIGURED_FLUID_CONDITIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_FLUID_CONDITION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<ConfiguredItemCondition<?, ?>> CONFIGURED_ITEM_CONDITIONS = DeferredRegister.create(ApoliDynamicRegistries.CONFIGURED_ITEM_CONDITION_KEY.location(), Apoli.MODID);

	public static void initialize() {
		ApoliBuiltinRegistries.CONFIGURED_POWERS = CONFIGURED_POWERS.makeRegistry(() -> new RegistryBuilder<ConfiguredPower<?, ?>>().hasTags().disableSaving());

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

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		CONFIGURED_POWERS.register(bus);

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
	}
}
