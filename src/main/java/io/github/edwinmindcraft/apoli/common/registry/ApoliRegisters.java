package io.github.edwinmindcraft.apoli.common.registry;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.util.PowerGrantingItem;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.component.IPowerDataCache;
import io.github.edwinmindcraft.apoli.api.power.factory.*;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryBuilder;

public class ApoliRegisters {
	public static final DeferredRegister<PowerFactory<?>> POWER_FACTORIES = DeferredRegister.create(ApoliRegistries.POWER_FACTORY_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<EntityCondition<?>> ENTITY_CONDITIONS = DeferredRegister.create(ApoliRegistries.ENTITY_CONDITION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<ItemCondition<?>> ITEM_CONDITIONS = DeferredRegister.create(ApoliRegistries.ITEM_CONDITION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<BlockCondition<?>> BLOCK_CONDITIONS = DeferredRegister.create(ApoliRegistries.BLOCK_CONDITION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<DamageCondition<?>> DAMAGE_CONDITIONS = DeferredRegister.create(ApoliRegistries.DAMAGE_CONDITION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<FluidCondition<?>> FLUID_CONDITIONS = DeferredRegister.create(ApoliRegistries.FLUID_CONDITION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<BiomeCondition<?>> BIOME_CONDITIONS = DeferredRegister.create(ApoliRegistries.BIOME_CONDITION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<EntityAction<?>> ENTITY_ACTIONS = DeferredRegister.create(ApoliRegistries.ENTITY_ACTION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<ItemAction<?>> ITEM_ACTIONS = DeferredRegister.create(ApoliRegistries.ITEM_ACTION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<BlockAction<?>> BLOCK_ACTIONS = DeferredRegister.create(ApoliRegistries.BLOCK_ACTION_KEY.location(), Apoli.MODID);

	public static final DeferredRegister<BiEntityCondition<?>> BIENTITY_CONDITIONS = DeferredRegister.create(ApoliRegistries.BIENTITY_CONDITION_KEY.location(), Apoli.MODID);
	public static final DeferredRegister<BiEntityAction<?>> BIENTITY_ACTIONS = DeferredRegister.create(ApoliRegistries.BIENTITY_ACTION_KEY.location(), Apoli.MODID);

	//TODO Builtin registries for every type of action & condition.

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Apoli.MODID);

	public static void initialize() {
		ApoliRegistries.POWER_FACTORY = POWER_FACTORIES.makeRegistry(ApoliRegistries.POWER_FACTORY_CLASS, () -> new RegistryBuilder<PowerFactory<?>>().disableSaving().hasTags());
		ApoliRegistries.ENTITY_CONDITION = ENTITY_CONDITIONS.makeRegistry(ApoliRegistries.ENTITY_CONDITION_CLASS, () -> new RegistryBuilder<EntityCondition<?>>().disableSaving().hasTags());
		ApoliRegistries.ITEM_CONDITION = ITEM_CONDITIONS.makeRegistry(ApoliRegistries.ITEM_CONDITION_CLASS, () -> new RegistryBuilder<ItemCondition<?>>().disableSaving().hasTags());
		ApoliRegistries.BLOCK_CONDITION = BLOCK_CONDITIONS.makeRegistry(ApoliRegistries.BLOCK_CONDITION_CLASS, () -> new RegistryBuilder<BlockCondition<?>>().disableSaving().hasTags());
		ApoliRegistries.DAMAGE_CONDITION = DAMAGE_CONDITIONS.makeRegistry(ApoliRegistries.DAMAGE_CONDITION_CLASS, () -> new RegistryBuilder<DamageCondition<?>>().disableSaving().hasTags());
		ApoliRegistries.FLUID_CONDITION = FLUID_CONDITIONS.makeRegistry(ApoliRegistries.FLUID_CONDITION_CLASS, () -> new RegistryBuilder<FluidCondition<?>>().disableSaving().hasTags());
		ApoliRegistries.BIOME_CONDITION = BIOME_CONDITIONS.makeRegistry(ApoliRegistries.BIOME_CONDITION_CLASS, () -> new RegistryBuilder<BiomeCondition<?>>().disableSaving().hasTags());
		ApoliRegistries.ENTITY_ACTION = ENTITY_ACTIONS.makeRegistry(ApoliRegistries.ENTITY_ACTION_CLASS, () -> new RegistryBuilder<EntityAction<?>>().disableSaving().hasTags());
		ApoliRegistries.ITEM_ACTION = ITEM_ACTIONS.makeRegistry(ApoliRegistries.ITEM_ACTION_CLASS, () -> new RegistryBuilder<ItemAction<?>>().disableSaving().hasTags());
		ApoliRegistries.BLOCK_ACTION = BLOCK_ACTIONS.makeRegistry(ApoliRegistries.BLOCK_ACTION_CLASS, () -> new RegistryBuilder<BlockAction<?>>().disableSaving().hasTags());
		ApoliRegistries.BIENTITY_CONDITION = BIENTITY_CONDITIONS.makeRegistry(ApoliRegistries.BIENTITY_CONDITION_CLASS, () -> new RegistryBuilder<BiEntityCondition<?>>().disableSaving().hasTags());
		ApoliRegistries.BIENTITY_ACTION = BIENTITY_ACTIONS.makeRegistry(ApoliRegistries.BIENTITY_ACTION_CLASS, () -> new RegistryBuilder<BiEntityAction<?>>().disableSaving().hasTags());

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		POWER_FACTORIES.register(bus);
		ENTITY_CONDITIONS.register(bus);
		ITEM_CONDITIONS.register(bus);
		BLOCK_CONDITIONS.register(bus);
		DAMAGE_CONDITIONS.register(bus);
		FLUID_CONDITIONS.register(bus);
		BIOME_CONDITIONS.register(bus);
		ENTITY_ACTIONS.register(bus);
		ITEM_ACTIONS.register(bus);
		BLOCK_ACTIONS.register(bus);
		BIENTITY_CONDITIONS.register(bus);
		BIENTITY_ACTIONS.register(bus);

		RECIPE_SERIALIZERS.register(bus);
		bus.addListener(ApoliRegisters::registerCapabilities);
	}

	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(IPowerContainer.class);
		event.register(IPowerDataCache.class);
		event.register(PowerGrantingItem.class);
	}
}
