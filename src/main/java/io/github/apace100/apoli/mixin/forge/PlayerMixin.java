package io.github.apace100.apoli.mixin.forge;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.util.ModifyPlayerSpawnCache;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public class PlayerMixin implements ModifyPlayerSpawnCache {
    @Unique
    ResourceKey<ConfiguredPower<?, ?>> apoli$activeModifyPlayerSpawnPower;

    @Override
    public void setActiveSpawnPower(ResourceKey<ConfiguredPower<?, ?>> key) {
        this.apoli$activeModifyPlayerSpawnPower = key;
    }

    @Override
    public ResourceKey<ConfiguredPower<?, ?>> getActiveSpawnPower() {
        return this.apoli$activeModifyPlayerSpawnPower;
    }

    @Override
    public void removeActiveSpawnPower() {
        this.apoli$activeModifyPlayerSpawnPower = null;
    }
}
