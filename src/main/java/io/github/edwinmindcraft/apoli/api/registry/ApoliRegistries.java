package io.github.edwinmindcraft.apoli.api.registry;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.power.factory.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ApoliRegistries {
	public static final ResourceKey<Registry<PowerFactory<?>>> POWER_FACTORY_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("power_factory"));
	public static final ResourceKey<Registry<EntityCondition<?>>> ENTITY_CONDITION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("entity_condition"));
	public static final ResourceKey<Registry<ItemCondition<?>>> ITEM_CONDITION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("item_condition"));
	public static final ResourceKey<Registry<BlockCondition<?>>> BLOCK_CONDITION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("block_condition"));
	public static final ResourceKey<Registry<DamageCondition<?>>> DAMAGE_CONDITION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("damage_condition"));
	public static final ResourceKey<Registry<FluidCondition<?>>> FLUID_CONDITION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("fluid_condition"));
	public static final ResourceKey<Registry<BiomeCondition<?>>> BIOME_CONDITION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("biome_condition"));
	public static final ResourceKey<Registry<EntityAction<?>>> ENTITY_ACTION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("entity_action"));
	public static final ResourceKey<Registry<ItemAction<?>>> ITEM_ACTION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("item_action"));
	public static final ResourceKey<Registry<BlockAction<?>>> BLOCK_ACTION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("block_action"));
	public static final ResourceKey<Registry<BiEntityCondition<?>>> BIENTITY_CONDITION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("bientity_condition"));
	public static final ResourceKey<Registry<BiEntityAction<?>>> BIENTITY_ACTION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("bientity_action"));
	public static final ResourceKey<Registry<ModifierOperation>> MODIFIER_OPERATION_KEY = ResourceKey.createRegistryKey(ApoliAPI.identifier("modifier"));

	public static final Registry<PowerFactory<?>> POWER_FACTORY = new RegistryBuilder<>(POWER_FACTORY_KEY).sync(true).create();
	public static final Registry<EntityCondition<?>> ENTITY_CONDITION = new RegistryBuilder<>(ENTITY_CONDITION_KEY).sync(true).create();
	public static final Registry<ItemCondition<?>> ITEM_CONDITION = new RegistryBuilder<>(ITEM_CONDITION_KEY).sync(true).create();
	public static final Registry<BlockCondition<?>> BLOCK_CONDITION = new RegistryBuilder<>(BLOCK_CONDITION_KEY).sync(true).create();
	public static final Registry<DamageCondition<?>> DAMAGE_CONDITION = new RegistryBuilder<>(DAMAGE_CONDITION_KEY).sync(true).create();
	public static final Registry<FluidCondition<?>> FLUID_CONDITION = new RegistryBuilder<>(FLUID_CONDITION_KEY).sync(true).create();
	public static final Registry<BiomeCondition<?>> BIOME_CONDITION = new RegistryBuilder<>(BIOME_CONDITION_KEY).sync(true).create();
	public static final Registry<EntityAction<?>> ENTITY_ACTION = new RegistryBuilder<>(ENTITY_ACTION_KEY).sync(true).create();
	public static final Registry<ItemAction<?>> ITEM_ACTION = new RegistryBuilder<>(ITEM_ACTION_KEY).sync(true).create();
	public static final Registry<BlockAction<?>> BLOCK_ACTION = new RegistryBuilder<>(BLOCK_ACTION_KEY).sync(true).create();
	public static final Registry<BiEntityCondition<?>> BIENTITY_CONDITION = new RegistryBuilder<>(BIENTITY_CONDITION_KEY).sync(true).create();
	public static final Registry<BiEntityAction<?>> BIENTITY_ACTION = new RegistryBuilder<>(BIENTITY_ACTION_KEY).sync(true).create();
	public static final Registry<ModifierOperation> MODIFIER_OPERATION = new RegistryBuilder<>(MODIFIER_OPERATION_KEY).sync(true).onAdd(ApoliRegistries::aliasApoliToMinecraft).create();

	private static <T> void aliasApoliToMinecraft(Registry<T> registry, int id, ResourceKey<T> key, T value) {
		ResourceLocation location = key.location();
		if (Apoli.MODID.equals(location.getNamespace()))
			registry.addAlias(new ResourceLocation(location.getPath()), location);
	}

	public static void bootstrap(IEventBus bus) {
		bus.addListener((NewRegistryEvent event) -> {
			event.register(POWER_FACTORY);
			event.register(ENTITY_CONDITION);
			event.register(ITEM_CONDITION);
			event.register(BLOCK_CONDITION);
			event.register(DAMAGE_CONDITION);
			event.register(FLUID_CONDITION);
			event.register(BIOME_CONDITION);
			event.register(ENTITY_ACTION);
			event.register(ITEM_ACTION);
			event.register(BLOCK_ACTION);
			event.register(BIENTITY_CONDITION);
			event.register(BIENTITY_ACTION);
			event.register(MODIFIER_OPERATION);
		});
	}
}
