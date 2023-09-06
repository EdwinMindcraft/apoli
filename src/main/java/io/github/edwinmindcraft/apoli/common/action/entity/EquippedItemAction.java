package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.edwinmindcraft.apoli.api.VariableAccess;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.EquippedItemConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EquippedItemAction extends EntityAction<EquippedItemConfiguration> {

	public EquippedItemAction() {
		super(EquippedItemConfiguration.CODEC);
	}

	@Override
	public void execute(EquippedItemConfiguration configuration, Entity entity) {
		if (entity instanceof LivingEntity living)
			ConfiguredItemAction.execute(configuration.action(), entity.level(), VariableAccess.slot(living, configuration.slot()));
	}
}
