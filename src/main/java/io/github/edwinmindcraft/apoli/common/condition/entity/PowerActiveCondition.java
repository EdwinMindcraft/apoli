package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.PowerReference;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import net.minecraft.world.entity.Entity;

public class PowerActiveCondition extends EntityCondition<PowerReference> {

	public PowerActiveCondition() {
		super(PowerReference.codec("power"));
	}

	@Override
	public boolean check(PowerReference configuration, Entity entity) {
		PowerContainer container = PowerContainer.get(entity);
		return container != null && container.hasPower(configuration.power()) && container.getPower(configuration.power()).value().isActive(entity);
	}
}
