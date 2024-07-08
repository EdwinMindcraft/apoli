package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.apoli.common.action.configuration.PowerSourceConfiguration;
import net.minecraft.world.entity.Entity;

public class PowerCondition extends EntityCondition<PowerSourceConfiguration> {

	public PowerCondition() {super(PowerSourceConfiguration.OPTIONAL_CODEC);}

	@Override
	public boolean check(PowerSourceConfiguration configuration, Entity entity) {
		PowerContainer container = PowerContainer.get(entity);
		return container != null && configuration.source() == null ? container.hasPower(configuration.power().power()) : container.hasPower(configuration.power().power(), configuration.source());
	}
}
