package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.PowerSourceConfiguration;
import net.minecraft.world.entity.Entity;

public class GrantPowerAction extends EntityAction<PowerSourceConfiguration> {
	public GrantPowerAction() {
		super(PowerSourceConfiguration.CODEC);
	}

	@Override
	public void execute(PowerSourceConfiguration configuration, Entity entity) {
		PowerContainer.get(entity).ifPresent(x -> {
			x.addPower(configuration.power().power(), configuration.source());
			x.sync();
		});
	}
}
