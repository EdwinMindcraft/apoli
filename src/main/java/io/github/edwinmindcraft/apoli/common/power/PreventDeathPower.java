package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredDamageCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.PreventDeathConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class PreventDeathPower extends PowerFactory<PreventDeathConfiguration> {

	public static boolean tryPreventDeath(Entity entity, DamageSource source, float amount) {
		Optional<ConfiguredPower<PreventDeathConfiguration, PreventDeathPower>> first = PowerContainer.getPowers(entity, ApoliPowers.PREVENT_DEATH.get()).stream()
				.filter(x -> ConfiguredDamageCondition.check(x.value().getConfiguration().condition(), source, amount)).map(Holder::value).findFirst();
		first.ifPresent(x -> {
			if (entity instanceof LivingEntity living)
				living.setHealth(1.0F);
			ConfiguredEntityAction.execute(x.getConfiguration().action(), entity);
		});
		return first.isPresent();
	}

	public PreventDeathPower() {
		super(PreventDeathConfiguration.CODEC);
	}
}
