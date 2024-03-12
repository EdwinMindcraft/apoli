package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.PowerSourceConfiguration;
import net.minecraft.world.entity.Entity;

public class RevokePowerAction extends EntityAction<PowerSourceConfiguration> {
	public RevokePowerAction() {
		super(PowerSourceConfiguration.CODEC);
	}

	@Override
	public void execute(PowerSourceConfiguration configuration, Entity entity) {
		PowerContainer.get(entity).ifPresent(x -> {
			x.removePower(configuration.power().power(), configuration.source());
			x.sync();
		});
	}
}
