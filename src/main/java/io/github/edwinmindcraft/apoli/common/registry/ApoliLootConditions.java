package io.github.edwinmindcraft.apoli.common.registry;

import io.github.apace100.apoli.util.PowerLootCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ApoliLootConditions {
	public static final DeferredHolder<LootItemConditionType, LootItemConditionType> POWER_LOOT_CONDITION = ApoliRegisters.LOOT_CONDITIONS.register("power", () -> new LootItemConditionType(PowerLootCondition.CODEC));

	public static void bootstrap() {
	}
}
