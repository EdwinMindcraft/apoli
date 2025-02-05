package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.access.PowerCraftingInventory;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.minecraft.world.inventory.CraftingContainer;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;

@Mixin(CraftingContainer.class)
public class CraftingInventoryMixin implements PowerCraftingInventory {

	private ConfiguredPower<?, ?> apoli$CachedPower;

	@Override
	public void setPower(ConfiguredPower<?, ?> power) {
		this.apoli$CachedPower = power;
	}

	@Override
	@Nullable
	public ConfiguredPower<?, ?> getPower() {
		return this.apoli$CachedPower;
	}
}
