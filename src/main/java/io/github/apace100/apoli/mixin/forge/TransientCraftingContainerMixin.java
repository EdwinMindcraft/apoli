package io.github.apace100.apoli.mixin.forge;

import io.github.apace100.apoli.access.PowerCraftingInventory;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.minecraft.world.inventory.TransientCraftingContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(TransientCraftingContainer.class)
public class TransientCraftingContainerMixin implements PowerCraftingInventory {


    @Unique
    private ConfiguredPower<?, ?> apoli$cachedPower;

    @Override
    public void setPower(ConfiguredPower<?, ?> power) {
        this.apoli$cachedPower = power;
    }

    @Override
    @Nullable
    public ConfiguredPower<?, ?> getPower() {
        return this.apoli$cachedPower;
    }
}
