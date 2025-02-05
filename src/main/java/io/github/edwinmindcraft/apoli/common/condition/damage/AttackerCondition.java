package io.github.edwinmindcraft.apoli.common.condition.damage;

import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.DamageCondition;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class AttackerCondition extends DamageCondition<FieldConfiguration<Optional<ConfiguredEntityCondition<?, ?>>>> {

	public AttackerCondition() {
		super(FieldConfiguration.optionalCodec(ConfiguredEntityCondition.CODEC, "entity_condition"));
	}

	@Override
	protected boolean check(FieldConfiguration<Optional<ConfiguredEntityCondition<?, ?>>> configuration, DamageSource source, float amount) {
		Entity attacker = source.getEntity();
		return attacker instanceof LivingEntity le ? configuration.value().map(x -> x.check(le)).orElse(true) : Boolean.valueOf(false);
	}
}
