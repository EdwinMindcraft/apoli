package io.github.edwinmindcraft.apoli.common.util;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.ModifyDamageDealtPower;
import io.github.edwinmindcraft.apoli.common.power.ModifyDamageTakenPower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyDamageDealtConfiguration;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyDamageTakenConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Objects;

public interface LivingDamageCache {
	List<Holder<ConfiguredPower<ModifyDamageTakenConfiguration, ModifyDamageTakenPower>>> getModifyDamageTakenPowers();

	List<Holder<ConfiguredPower<ModifyDamageDealtConfiguration, ModifyDamageDealtPower>>> getModifyDamageDealtPowers();

	void setModifyDamageTakenPowers(List<Holder<ConfiguredPower<ModifyDamageTakenConfiguration, ModifyDamageTakenPower>>> values);

	void setModifyDamageDealtPowers(List<Holder<ConfiguredPower<ModifyDamageDealtConfiguration, ModifyDamageDealtPower>>> values);

	void bypassDamageCheck(boolean value);

	boolean bypassesDamageCheck();

	void setArmorValues(int apply, int damage);

	default void gatherDamagePowers(Entity entity, DamageSource source, float amount) {
		//This method is basically a grouping of everything that needs to be computed when an entity is attacked.
		this.setModifyDamageTakenPowers(PowerContainer.getPowers(entity,
				ApoliPowers.MODIFY_DAMAGE_TAKEN.get(),
				x -> x.value().isActive(entity) && x.value().getFactory().check(x.value(), entity, source, amount)));
        ModifyDamageDealtPower dealtFactory = source.is(DamageTypeTags.IS_PROJECTILE) ? ApoliPowers.MODIFY_PROJECTILE_DAMAGE.get() : ApoliPowers.MODIFY_DAMAGE_DEALT.get();
        this.setModifyDamageDealtPowers(PowerContainer.getPowers(source.getEntity(),
                dealtFactory,
				x -> x.value().isActive(Objects.requireNonNull(source.getEntity())) && dealtFactory.check(x.value(), Objects.requireNonNull(source.getEntity()), entity, source, amount)));
		this.bypassDamageCheck(false);
		int apply = 0;
		int damage = 0;

		for (Holder<ConfiguredPower<ModifyDamageTakenConfiguration, ModifyDamageTakenPower>> holder : this.getModifyDamageTakenPowers()) {
			ConfiguredPower<ModifyDamageTakenConfiguration, ModifyDamageTakenPower> power = holder.value();
			ModifyDamageTakenPower factory = power.getFactory();
			if (factory.modifiesArmorApplicance(power)) {
				if (factory.checkArmorApplicance(power, entity)) ++apply;
				else --apply;
			}
			if (factory.modifiesArmorDamaging(power)) {
				if (factory.checkArmorDamaging(power, entity)) ++damage;
				else --damage;
			}
		}
		this.setArmorValues(apply, damage);
	}
}
