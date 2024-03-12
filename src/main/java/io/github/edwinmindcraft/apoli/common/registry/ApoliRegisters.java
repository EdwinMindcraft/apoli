package io.github.edwinmindcraft.apoli.common.registry;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.access.EntityLinkedItemStack;
import io.github.apace100.apoli.util.PowerGrantingItem;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.factory.*;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

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

	public static final DeferredRegister<ModifierOperation> MODIFIER_OPERATIONS = DeferredRegister.create(ApoliRegistries.MODIFIER_OPERATION_KEY.location(), Apoli.MODID);

	//TODO Builtin registries for every type of action & condition.

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Apoli.MODID);
	public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Apoli.MODID);
	public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, Apoli.MODID);
	public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, Apoli.MODID);
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Apoli.MODID);

	public static void initialize(IEventBus bus) {
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
		MODIFIER_OPERATIONS.register(bus);

		RECIPE_SERIALIZERS.register(bus);
		ARGUMENT_TYPES.register(bus);
		LOOT_FUNCTIONS.register(bus);
		LOOT_CONDITIONS.register(bus);
		ATTACHMENT_TYPES.register(bus);
		bus.addListener(ApoliRegisters::registerCapabilities);
	}

	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(PowerContainer.class);
		event.register(PowerGrantingItem.class);
		event.register(EntityLinkedItemStack.class);
	}
}
