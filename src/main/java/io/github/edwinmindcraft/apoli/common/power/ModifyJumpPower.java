package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.power.ValueModifyingPowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyJumpConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.entity.Entity;

public class ModifyJumpPower extends ValueModifyingPowerFactory<ModifyJumpConfiguration> {
	public static double apply(Entity player, double baseValue) {
		return PowerContainer.modify(player, ApoliPowers.MODIFY_JUMP.get(), baseValue, x -> true, x -> x.value().getFactory().execute(x.value(), player));
	}

	public ModifyJumpPower() {
		super(ModifyJumpConfiguration.CODEC);
	}

	public void execute(ConfiguredPower<ModifyJumpConfiguration, ?> configuration, Entity player) {
		ConfiguredEntityAction.execute(configuration.getConfiguration().condition(), player);
	}
}
