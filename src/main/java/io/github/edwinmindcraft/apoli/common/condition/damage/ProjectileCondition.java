package io.github.edwinmindcraft.apoli.common.condition.damage;

import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.DamageCondition;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public class ProjectileCondition extends DamageCondition<FieldConfiguration<Optional<EntityType<?>>>> {

	public ProjectileCondition() {
		super(FieldConfiguration.optionalCodec(SerializableDataTypes.ENTITY_TYPE, "projectile"));
	}

	@Override
	protected boolean check(FieldConfiguration<Optional<EntityType<?>>> configuration, DamageSource source, float amount) {
		if (source.is(DamageTypeTags.IS_PROJECTILE)) {
			Entity projectile = source.getDirectEntity();
			return projectile != null && configuration.value().map(projectile.getType()::equals).orElse(true);
		}
		return false;
	}
}
