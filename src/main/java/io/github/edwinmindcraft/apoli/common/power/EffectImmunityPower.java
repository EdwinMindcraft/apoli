package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.EffectImmunityConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;

public class EffectImmunityPower extends PowerFactory<EffectImmunityConfiguration> {
	public static boolean isImmune(Entity player, MobEffectInstance effect) {
		return PowerContainer.getPowers(player, ApoliPowers.EFFECT_IMMUNITY.get()).stream().anyMatch(x -> x.value().getFactory().isImmune(x.value(), player, effect));
	}

	public EffectImmunityPower() {
		super(EffectImmunityConfiguration.CODEC);
	}

	public boolean isImmune(ConfiguredPower<EffectImmunityConfiguration, ?> configuration, Entity player, MobEffect effect) {
		return configuration.getConfiguration().inverted() ^ configuration.getConfiguration().effects().getContent().contains(effect);
	}

	public boolean isImmune(ConfiguredPower<EffectImmunityConfiguration, ?> configuration, Entity player, MobEffectInstance effect) {
		return this.isImmune(configuration, player, effect.getEffect());
	}
}
