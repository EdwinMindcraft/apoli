package io.github.apace100.apoli.action.entity;

import io.github.apace100.apoli.action.configuration.AreaOfEffectConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class AreaOfEffectAction extends EntityAction<AreaOfEffectConfiguration> {
	public AreaOfEffectAction() {
		super(AreaOfEffectConfiguration.CODEC);
	}

	@Override
	public void execute(@NotNull AreaOfEffectConfiguration configuration, @NotNull Entity entity) {
		double diameter = configuration.radius() * 2;

		for (Entity check : entity.level().getEntitiesOfClass(Entity.class, AABB.ofSize(entity.getPosition(1F), diameter, diameter, diameter))) {
			if (check == entity && !configuration.includeTarget())
				continue;
			if (ConfiguredBiEntityCondition.check(configuration.condition(), entity, check) && check.distanceToSqr(entity) < Mth.square(configuration.radius()))
				configuration.action().value().execute(entity, check);
		}
	}
}

