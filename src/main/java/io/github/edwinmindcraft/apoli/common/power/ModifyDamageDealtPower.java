package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.*;
import io.github.edwinmindcraft.apoli.api.power.factory.power.ValueModifyingPowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyDamageDealtConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ModifyDamageDealtPower extends ValueModifyingPowerFactory<ModifyDamageDealtConfiguration> {
	public static float modifyMelee(@Nullable Entity entity, Entity target, DamageSource source, float amount) {
		return PowerContainer.modify(entity, ApoliPowers.MODIFY_DAMAGE_DEALT.get(), amount, x -> x.value().getFactory().check(x.value(), Objects.requireNonNull(entity), target, source, amount), x -> x.value().getFactory().execute(x.value(), Objects.requireNonNull(entity), target));
	}

	public static float modifyProjectile(@Nullable Entity entity, Entity target, DamageSource source, float amount) {
		return PowerContainer.modify(entity, ApoliPowers.MODIFY_PROJECTILE_DAMAGE.get(), amount, x -> x.value().getFactory().check(x.value(), Objects.requireNonNull(entity), target, source, amount), x -> x.value().getFactory().execute(x.value(), Objects.requireNonNull(entity), target));
	}

	public static float modifyMeleeNoExec(@Nullable Entity entity, Entity target, DamageSource source, float amount) {
		return PowerContainer.modify(entity, ApoliPowers.MODIFY_DAMAGE_DEALT.get(), amount, x -> x.value().getFactory().check(x.value(), Objects.requireNonNull(entity), target, source, amount), x -> {});
	}

	public static float modifyProjectileNoExec(@Nullable Entity entity, Entity target, DamageSource source, float amount) {
		return PowerContainer.modify(entity, ApoliPowers.MODIFY_PROJECTILE_DAMAGE.get(), amount, x -> x.value().getFactory().check(x.value(), Objects.requireNonNull(entity), target, source, amount), x -> {});
	}


	public ModifyDamageDealtPower() {
		super(ModifyDamageDealtConfiguration.CODEC);
	}

	public boolean check(ConfiguredPower<ModifyDamageDealtConfiguration, ?> config, Entity entity, @Nullable Entity target, DamageSource source, float amount) {
		ModifyDamageDealtConfiguration configuration = config.getConfiguration();
		return ConfiguredDamageCondition.check(configuration.damageCondition(), source, amount) &&
			   (target == null || ConfiguredEntityCondition.check(configuration.targetCondition(), target)) &&
			   (target == null || ConfiguredBiEntityCondition.check(configuration.biEntityCondition(), entity, target));
	}

	public void execute(ConfiguredPower<ModifyDamageDealtConfiguration, ?> config, Entity entity, @Nullable Entity target) {
		ModifyDamageDealtConfiguration configuration = config.getConfiguration();
		ConfiguredEntityAction.execute(configuration.selfAction(), entity);
		if (target != null) {
			ConfiguredEntityAction.execute(configuration.targetAction(), target);
			ConfiguredBiEntityAction.execute(configuration.biEntityAction(), entity, target);
		}
	}
}
