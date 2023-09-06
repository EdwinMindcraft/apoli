package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.apace100.apoli.util.MiscUtil;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.DamageConfiguration;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class DamageAction extends EntityAction<DamageConfiguration> {

	public DamageAction() {
		super(DamageConfiguration.CODEC);
	}

	@Override
	public void execute(DamageConfiguration configuration, Entity entity) {
        DamageSource source = MiscUtil.createDamageSource(
                entity.damageSources(), configuration.source(), configuration.damageType());
        entity.hurt(source, configuration.amount());
	}
}
