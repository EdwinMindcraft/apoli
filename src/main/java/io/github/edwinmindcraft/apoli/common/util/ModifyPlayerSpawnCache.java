package io.github.edwinmindcraft.apoli.common.util;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.minecraft.resources.ResourceKey;

public interface ModifyPlayerSpawnCache {

    void setActiveSpawnPower(ResourceKey<ConfiguredPower<?, ?>> key);

    ResourceKey<ConfiguredPower<?, ?>> getActiveSpawnPower();

    void removeActiveSpawnPower();

}
