package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.VariableAccess;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ItemOnItemConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemOnItemPower extends PowerFactory<ItemOnItemConfiguration> {
	public static boolean execute(Entity entity, Slot self, SlotAccess other) {
		List<ConfiguredPower<ItemOnItemConfiguration, ItemOnItemPower>> powers = IPowerContainer.getPowers(entity, ApoliPowers.ITEM_ON_ITEM.get()).stream().filter(x -> x.getFactory().check(x, entity, self.getItem(), other.get())).toList();
		powers.forEach(x -> x.getFactory().apply(x, entity, self, other));
		return !powers.isEmpty();
	}

	public ItemOnItemPower() {
		super(ItemOnItemConfiguration.CODEC);
	}

	public boolean check(ConfiguredPower<ItemOnItemConfiguration, ?> power, Entity entity, ItemStack self, ItemStack other) {
		return power.getConfiguration().check(entity.level, other, self);
	}

	public void apply(ConfiguredPower<ItemOnItemConfiguration, ?> power, Entity entity, Slot self, SlotAccess other) {
		ItemOnItemConfiguration configuration = power.getConfiguration();
		configuration.execute(entity, VariableAccess.slot(other), VariableAccess.slot(self), self);
	}
}
