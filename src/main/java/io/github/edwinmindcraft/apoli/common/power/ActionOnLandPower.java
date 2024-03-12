package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.HolderConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.entity.Entity;

public class ActionOnLandPower extends PowerFactory<HolderConfiguration<ConfiguredEntityAction<?, ?>>> {
	public static void execute(Entity player) {
		var ls = PowerContainer.getPowers(player, ApoliPowers.ACTION_ON_LAND.get());
		ls.forEach(x -> x.value().getFactory().executeAction(x.value(), player));
	}

	public ActionOnLandPower() {
		super(HolderConfiguration.required(ConfiguredEntityAction.required("entity_action")));
	}

	public void executeAction(ConfiguredPower<HolderConfiguration<ConfiguredEntityAction<?, ?>>, ?> config, Entity player) {
		ConfiguredEntityAction.execute(config.getConfiguration().holder(), player);
	}
}
