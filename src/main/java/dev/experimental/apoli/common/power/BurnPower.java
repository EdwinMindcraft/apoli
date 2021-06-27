package dev.experimental.apoli.common.power;

import dev.experimental.apoli.api.power.factory.PowerFactory;
import dev.experimental.apoli.common.power.configuration.BurnConfiguration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class BurnPower extends PowerFactory<BurnConfiguration> {
	public BurnPower() {
		super(BurnConfiguration.CODEC);
		this.ticking();
	}

	@Override
	protected void tick(BurnConfiguration configuration, LivingEntity player) {
		player.setOnFireFor(configuration.duration());
	}

	@Override
	protected int tickInterval(BurnConfiguration configuration, LivingEntity player) {
		return configuration.interval();
	}
}
