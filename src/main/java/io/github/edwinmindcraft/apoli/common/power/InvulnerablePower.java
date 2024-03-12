package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.HolderConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredDamageCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class InvulnerablePower extends PowerFactory<HolderConfiguration<ConfiguredDamageCondition<?, ?>>> {

	public static boolean isInvulnerableTo(Entity entity, DamageSource source) {
		return PowerContainer.getPowers(entity, ApoliPowers.INVULNERABILITY.get()).stream().anyMatch(x -> ConfiguredDamageCondition.check(x.value().getConfiguration().holder(), source, Float.NaN));
	}

	public InvulnerablePower() {
		super(HolderConfiguration.required(ConfiguredDamageCondition.required("damage_condition")));
	}
}
