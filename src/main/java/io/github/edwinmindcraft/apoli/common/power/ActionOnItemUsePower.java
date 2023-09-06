package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ActionOnItemUseConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.Mutable;

public class ActionOnItemUsePower extends PowerFactory<ActionOnItemUseConfiguration> {
	public static void execute(Entity player, ItemStack stack, Mutable<ItemStack> target) {
		IPowerContainer component = ApoliAPI.getPowerContainer(player);
		if (component != null)
			component.getPowers(ApoliPowers.ACTION_ON_ITEM_USE.get()).stream()
					.filter(x -> x.value().getFactory().doesApply(x.value(), player, stack))
					.forEach(x -> x.value().getFactory().executeActions(x.value(), player, target));
	}

	public ActionOnItemUsePower() {
		super(ActionOnItemUseConfiguration.CODEC);
	}

	public boolean doesApply(ConfiguredPower<ActionOnItemUseConfiguration, ?> factory, Entity player, ItemStack stack) {
		ActionOnItemUseConfiguration configuration = factory.getConfiguration();
		return ConfiguredItemCondition.check(configuration.itemCondition(), player.level(), stack);
	}

	public void executeActions(ConfiguredPower<ActionOnItemUseConfiguration, ?> factory, Entity player, Mutable<ItemStack> stack) {
		ActionOnItemUseConfiguration configuration = factory.getConfiguration();
		ConfiguredItemAction.execute(configuration.itemAction(), player.level(), stack);
		ConfiguredEntityAction.execute(configuration.entityAction(), player);
	}
}
