package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.DamageConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class DamageAction extends EntityAction<DamageConfiguration> {

	public DamageAction() {
		super(DamageConfiguration.CODEC);
	}

	@Override
	public void execute(DamageConfiguration configuration, Entity entity) {
		if (entity instanceof LivingEntity)
			entity.hurt(configuration.source(), configuration.amount());
	}
}
