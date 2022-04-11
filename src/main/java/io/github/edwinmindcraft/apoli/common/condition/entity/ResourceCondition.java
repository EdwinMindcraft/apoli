package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.ResourceComparisonConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.OptionalInt;

public class ResourceCondition extends EntityCondition<ResourceComparisonConfiguration> {

	public ResourceCondition() {
		super(ResourceComparisonConfiguration.CODEC);
	}

	@Override
	public boolean check(ResourceComparisonConfiguration configuration, Entity entity) {
		return IPowerContainer.get(entity).resolve().map(x -> x.getPower(configuration.resource().power())).map(power -> {
			if (entity instanceof Player player) {
				OptionalInt value = power.getValue(player);
				return value.isPresent() && configuration.comparison().check(value.getAsInt());
			}
			return false;
		}).orElse(false);
	}
}
