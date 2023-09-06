package io.github.edwinmindcraft.apoli.common.action.entity;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.apoli.common.action.configuration.DistanceFromPointConfiguration;
import net.minecraft.world.entity.Entity;

public class DistanceFromPointEntityCondition extends EntityCondition<DistanceFromPointConfiguration> {
	public DistanceFromPointEntityCondition(Codec<DistanceFromPointConfiguration> codec) {
		super(codec);
	}

	@Override
	protected boolean check(DistanceFromPointConfiguration configuration, Entity entity) {
		return configuration.test(null, entity.position(), entity.level());
	}
}
