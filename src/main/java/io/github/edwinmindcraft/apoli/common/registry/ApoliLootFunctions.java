package io.github.edwinmindcraft.apoli.common.registry;

import io.github.apace100.apoli.util.AddPowerLootFunction;
import io.github.apace100.apoli.util.RemovePowerLootFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ApoliLootFunctions {
	public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> ADD_POWER_LOOT_FUNCTION = ApoliRegisters.LOOT_FUNCTIONS.register("add_power", () -> new LootItemFunctionType(new AddPowerLootFunction.Serializer()));
	public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> REMOVE_POWER_LOOT_FUNCTION = ApoliRegisters.LOOT_FUNCTIONS.register("remove_power", () -> new LootItemFunctionType(new RemovePowerLootFunction.Serializer()));

	public static void bootstrap() {}
}
