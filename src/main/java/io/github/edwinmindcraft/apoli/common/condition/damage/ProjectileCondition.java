package io.github.edwinmindcraft.apoli.common.condition.damage;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.DamageCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.ProjectileConfiguration;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class ProjectileCondition extends DamageCondition<ProjectileConfiguration> {

	public ProjectileCondition() {
		super(ProjectileConfiguration.CODEC);
	}

	@Override
	protected boolean check(ProjectileConfiguration configuration, DamageSource source, float amount) {
		if (source.is(DamageTypeTags.IS_PROJECTILE)) {
			Entity projectile = source.getDirectEntity();
			return projectile != null && configuration.projectile().map(projectile.getType()::equals).orElse(true) && ConfiguredEntityCondition.check(configuration.projectileCondition(), projectile);
		}
		return false;
	}
}
