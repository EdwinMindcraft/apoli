package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.StackingStatusEffectConfiguration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.concurrent.atomic.AtomicInteger;

public class StackingStatusEffectPower extends PowerFactory<StackingStatusEffectConfiguration> {
	public StackingStatusEffectPower() {
		super(StackingStatusEffectConfiguration.CODEC);
		this.ticking(true);
	}

	public AtomicInteger getCurrentStacks(ConfiguredPower<StackingStatusEffectConfiguration, ?> configuration, Entity player) {
		return configuration.getPowerData(player, () -> new AtomicInteger(0));
	}

	public AtomicInteger getCurrentStacks(ConfiguredPower<StackingStatusEffectConfiguration, ?> configuration, PowerContainer player) {
		return configuration.getPowerData(player, () -> new AtomicInteger(0));
	}

	@Override
	public void tick(ConfiguredPower<StackingStatusEffectConfiguration, ?> configuration, Entity player) {
		AtomicInteger currentStacks = this.getCurrentStacks(configuration, player);
		StackingStatusEffectConfiguration config = configuration.getConfiguration();
		if (configuration.isActive(player)) {
			int i = currentStacks.addAndGet(1);
			if (i > config.max())
				currentStacks.set(config.max());
			if (i > 0)
				this.applyEffects(configuration, player);
		} else {
			int i = currentStacks.addAndGet(-1);
			if (i < config.min())
				currentStacks.set(config.min());
		}
	}

	@Override
	protected int tickInterval(StackingStatusEffectConfiguration configuration, Entity player) {
		return configuration.tickRate();
	}

	@Override
	public void serialize(ConfiguredPower<StackingStatusEffectConfiguration, ?> configuration, PowerContainer container, CompoundTag tag) {
		tag.putInt("CurrentStacks", this.getCurrentStacks(configuration, container).get());
	}

	@Override
	public void deserialize(ConfiguredPower<StackingStatusEffectConfiguration, ?> configuration, PowerContainer container, CompoundTag tag) {
		this.getCurrentStacks(configuration, container).set(tag.getInt("CurrentStacks"));
	}

	public void applyEffects(ConfiguredPower<StackingStatusEffectConfiguration, ?> configuration, Entity entity) {
		if (!(entity instanceof LivingEntity living) || entity.level().isClientSide())
			return;
		configuration.getConfiguration().effects().getContent().forEach(sei -> {
			int duration = configuration.getConfiguration().duration() * this.getCurrentStacks(configuration, living).get();
			if (duration > 0) {
				MobEffectInstance applySei = new MobEffectInstance(sei.getEffect(), duration, sei.getAmplifier(), sei.isAmbient(), sei.isVisible(), sei.showIcon());
				living.addEffect(applySei);
			}
		});
	}
}
