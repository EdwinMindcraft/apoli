package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredDamageCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.power.CooldownPowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.CombatHitActionConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class CombatHitActionPower extends CooldownPowerFactory.Simple<CombatHitActionConfiguration> {
	public static void perform(Entity attacker, Entity target, DamageSource source, float amount) {
		PowerContainer.getPowers(attacker, ApoliPowers.ACTION_ON_HIT.get()).forEach(x -> x.value().getFactory().onHit(x.value(), attacker, target, source, amount));
		PowerContainer.getPowers(target, ApoliPowers.ACTION_WHEN_HIT.get()).forEach(x -> x.value().getFactory().whenHit(x.value(), target, attacker, source, amount));
	}

	public CombatHitActionPower() {
		super(CombatHitActionConfiguration.CODEC);
	}

	public void whenHit(ConfiguredPower<CombatHitActionConfiguration, ?> power, Entity self, Entity attacker, DamageSource damageSource, float damageAmount) {
		if (this.canUse(power, self)) {
			if (ConfiguredBiEntityCondition.check(power.getConfiguration().biEntityCondition(), attacker, self) &&
				ConfiguredDamageCondition.check(power.getConfiguration().damageCondition(), damageSource, damageAmount)) {
				this.use(power, self);
				power.getConfiguration().biEntityAction().value().execute(attacker, self);
			}
		}
	}

	public void onHit(ConfiguredPower<CombatHitActionConfiguration, ?> power, Entity self, Entity target, DamageSource damageSource, float damageAmount) {
		if (this.canUse(power, self)) {
			if (ConfiguredBiEntityCondition.check(power.getConfiguration().biEntityCondition(), self, target) &&
				ConfiguredDamageCondition.check(power.getConfiguration().damageCondition(), damageSource, damageAmount)) {
				power.getConfiguration().biEntityAction().value().execute(self, target);
				this.use(power, self);
			}
		}
	}
}
