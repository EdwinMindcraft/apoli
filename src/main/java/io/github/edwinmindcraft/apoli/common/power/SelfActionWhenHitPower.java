package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredDamageCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.power.CooldownPowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ActionWhenHitConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class SelfActionWhenHitPower extends CooldownPowerFactory.Simple<ActionWhenHitConfiguration> {
	public static void execute(Entity player, DamageSource damageSource, float amount) {
		PowerContainer.getPowers(player, ApoliPowers.SELF_ACTION_WHEN_HIT.get()).forEach(x -> x.value().getFactory().whenHit(x.value(), player, damageSource, amount));
		PowerContainer.getPowers(player, ApoliPowers.ACTION_WHEN_DAMAGE_TAKEN.get()).forEach(x -> x.value().getFactory().whenHit(x.value(), player, damageSource, amount));
	}

	public SelfActionWhenHitPower() {
		super(ActionWhenHitConfiguration.CODEC);
	}

	public void whenHit(ConfiguredPower<ActionWhenHitConfiguration, ?> configuration, Entity player, DamageSource damageSource, float damageAmount) {
		if (ConfiguredDamageCondition.check(configuration.getConfiguration().damageCondition(), damageSource, damageAmount)) {
			if (this.canUse(configuration, player)) {
				ConfiguredEntityAction.execute(configuration.getConfiguration().entityAction(), player);
				this.use(configuration, player);
			}
		}
	}
}
