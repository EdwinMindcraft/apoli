package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.power.ActiveCooldownPowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.LaunchConfiguration;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class LaunchPower extends ActiveCooldownPowerFactory.Simple<LaunchConfiguration> {
	public LaunchPower() {
		super(LaunchConfiguration.CODEC);
	}

	@Override
	protected void execute(ConfiguredPower<LaunchConfiguration, ?> configuration, Entity player) {
		Level world = player.getCommandSenderWorld();
		if (!world.isClientSide()) {
			LaunchConfiguration config = configuration.getConfiguration();
			player.push(0, config.speed(), 0);
			player.hurtMarked = true;
			if (config.sound() != null)
				world.playSound(null, player.getX(), player.getY(), player.getZ(), config.sound(), SoundSource.NEUTRAL, 0.5F, 0.4F / (player.level().random.nextFloat() * 0.4F + 0.8F));
			if (player.level() instanceof ServerLevel serverWorld) {
				for (int i = 0; i < 4; ++i)
					serverWorld.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getRandomY(), player.getZ(), 8, player.level().random.nextGaussian(), 0.0D, player.level().random.nextGaussian(), 0.5);
			}
		}
	}
}
