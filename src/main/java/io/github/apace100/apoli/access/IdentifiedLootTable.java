package io.github.apace100.apoli.access;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataManager;

public interface IdentifiedLootTable {

    void setId(ResourceLocation id, LootDataManager lootManager);

    ResourceLocation getId();
}