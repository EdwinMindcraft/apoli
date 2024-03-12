package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.apoli.common.action.configuration.PowerSourceConfiguration;
import net.minecraft.world.entity.Entity;

public class PowerCondition extends EntityCondition<PowerSourceConfiguration> {

	public PowerCondition() {super(PowerSourceConfiguration.OPTIONAL_CODEC);}

	@Override
	public boolean check(PowerSourceConfiguration configuration, Entity entity) {
		return PowerContainer.get(entity).map(x -> configuration.source() == null ? x.hasPower(configuration.power().power()) : x.hasPower(configuration.power().power(), configuration.source())).orElse(false);
	}
}
