package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.ResourceComparisonConfiguration;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.OptionalInt;

public class ResourceCondition extends EntityCondition<ResourceComparisonConfiguration> {

	public ResourceCondition() {
		super(ResourceComparisonConfiguration.CODEC);
	}

	@Override
	public boolean check(ResourceComparisonConfiguration configuration, Entity entity) {
		PowerContainer container = PowerContainer.get(entity);
		if (container == null || !container.hasPower(configuration.resource().power()))
			return false;
		var power = container.getPower(configuration.resource().power());
		OptionalInt value = power.value().getValue(entity);
		return value.isPresent() && configuration.comparison().check(value.getAsInt());
	}
}
