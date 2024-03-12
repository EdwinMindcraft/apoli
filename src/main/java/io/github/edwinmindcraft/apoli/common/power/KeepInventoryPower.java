package io.github.edwinmindcraft.apoli.common.power;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.KeepInventoryConfiguration;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class KeepInventoryPower extends PowerFactory<KeepInventoryConfiguration> {
	public KeepInventoryPower() {
		super(KeepInventoryConfiguration.CODEC);
	}


	private NonNullList<ItemStack> access(ConfiguredPower<KeepInventoryConfiguration, ?> power, PowerContainer container) {
		return power.getPowerData(container, NonNullList::create);
	}

	private NonNullList<ItemStack> access(ConfiguredPower<KeepInventoryConfiguration, ?> power, Entity entity) {
		return power.getPowerData(entity, NonNullList::create);
	}

	public void captureItems(ConfiguredPower<KeepInventoryConfiguration, ?> power, Player player) {
		NonNullList<ItemStack> access = this.access(power, player);
		access.clear();
		KeepInventoryConfiguration config = power.getConfiguration();
		Inventory inventory = player.getInventory();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (config.isApplicableTo(i, player.level(), stack) && !EnchantmentHelper.hasVanishingCurse(stack)) {
				access.add(stack);
				inventory.setItem(i, ItemStack.EMPTY);
			} else
				access.add(ItemStack.EMPTY);
		}
	}

	public void restoreItems(ConfiguredPower<KeepInventoryConfiguration, ?> power, Player player) {
		Inventory inventory = player.getInventory();
		NonNullList<ItemStack> access = this.access(power, player);
		if (access == null || access.isEmpty()) {
			Apoli.LOGGER.error(KeepInventoryPower.class.getSimpleName() +
							   ": Tried to restore items without having saved any on entity \""
							   + player.getName().getString() + "\". Power may not have functioned correctly.");
			return;
		}
		if (inventory.getContainerSize() != access.size()) {
			Apoli.LOGGER.error(KeepInventoryPower.class.getSimpleName() +
							   ": Tried to restore items with differently sized inventory on entity \""
							   + player.getName().getString() + "\". Items may have been lost.");
		}
		for (int i = 0; i < inventory.getContainerSize() && i < access.size(); i++) {
			if (!access.get(i).isEmpty()) {
				inventory.setItem(i, access.get(i));
			}
		}
		access.clear();
	}

	@Override
	public void serialize(ConfiguredPower<KeepInventoryConfiguration, ?> configuration, PowerContainer container, CompoundTag tag) {
		ContainerHelper.saveAllItems(tag, this.access(configuration, container));
	}

	@Override
	public void deserialize(ConfiguredPower<KeepInventoryConfiguration, ?> configuration, PowerContainer container, CompoundTag tag) {
		NonNullList<ItemStack> access = this.access(configuration, container);
		while (access.size() <= Inventory.SLOT_OFFHAND)
			access.add(ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, access);
	}
}
