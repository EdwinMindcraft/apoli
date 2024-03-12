package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.power.ActiveCooldownPowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.FireProjectileConfiguration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class FireProjectilePower extends ActiveCooldownPowerFactory<FireProjectileConfiguration> {
	public FireProjectilePower() {
		super(FireProjectileConfiguration.CODEC);
		this.ticking();
	}

	protected DataContainer access(ConfiguredPower<FireProjectileConfiguration, ?> configuration, PowerContainer container) {
		return configuration.getPowerData(container, DataContainer::new);
	}

	@Override
	protected void execute(ConfiguredPower<FireProjectileConfiguration, ?> configuration, Entity player) {
		DataContainer dataContainer = this.access(configuration, ApoliAPI.getPowerContainer(player));
		dataContainer.isFiringProjectiles = true;
	}

	@Override
	protected long getLastUseTime(ConfiguredPower<FireProjectileConfiguration, ?> configuration, @Nullable PowerContainer container) {
		if (container != null)
			return this.access(configuration, container).lastUseTime;
		return Long.MAX_VALUE;
	}

	@Override
	protected void setLastUseTime(ConfiguredPower<FireProjectileConfiguration, ?> configuration, @Nullable PowerContainer container, long value) {
		if (container != null)
			this.access(configuration, container).lastUseTime = value;
	}

	@Override
	public void serialize(ConfiguredPower<FireProjectileConfiguration, ?> configuration, PowerContainer container, CompoundTag tag) {
		this.access(configuration, container).serialize(tag);
	}

	@Override
	public void deserialize(ConfiguredPower<FireProjectileConfiguration, ?> configuration, PowerContainer container, CompoundTag tag) {
		this.access(configuration, container).deserialize(tag);
	}

	@Override
	public void tick(ConfiguredPower<FireProjectileConfiguration, ?> configuration, Entity entity) {
		DataContainer dataContainer = this.access(configuration, ApoliAPI.getPowerContainer(entity));
		FireProjectileConfiguration config = configuration.getConfiguration();

		if (dataContainer.isFiringProjectiles) {
			long elapsed = this.getElapsedDuration(configuration, entity);
			if (!dataContainer.finishedStartDelay && (config.startDelay() == 0 || elapsed % config.startDelay() == 0))
				dataContainer.finishedStartDelay = true;
			if (!dataContainer.finishedStartDelay)
				return;
			if (config.interval() == 0) {
				config.playSound(entity);
				if (!entity.level().isClientSide()) {
					for (; dataContainer.shotProjectiles < config.projectileCount(); dataContainer.shotProjectiles++) {
						config.fireProjectile(entity);
					}
				}
				dataContainer.reset();
			} else if ((entity.level().getGameTime() - this.getLastUseTime(configuration, entity)) % config.interval() == 0) {
				dataContainer.shotProjectiles += 1;
				if (dataContainer.shotProjectiles <= config.projectileCount()) {
					config.playSound(entity);
					if (!entity.level().isClientSide())
						config.fireProjectile(entity);
				} else
					dataContainer.reset();
			}
		}
	}


	private static class DataContainer {
		private long lastUseTime = Long.MIN_VALUE;
		private int shotProjectiles;
		private boolean finishedStartDelay;
		private boolean isFiringProjectiles;

		public void serialize(CompoundTag tag) {
			tag.putLong("LastUseTime", this.lastUseTime);
			tag.putInt("ShotProjectiles", this.shotProjectiles);
			tag.putBoolean("FinishedStartDelay", this.finishedStartDelay);
			tag.putBoolean("IsFiringProjectiles", this.isFiringProjectiles);
		}

		public void reset() {
			this.shotProjectiles = 0;
			this.finishedStartDelay = false;
			this.isFiringProjectiles = false;
		}

		public void deserialize(CompoundTag tag) {
			this.lastUseTime = tag.getLong("LastUseTime");
			this.shotProjectiles = tag.getInt("ShotProjectiles");
			this.finishedStartDelay = tag.getBoolean("FinishedStartDelay");
			this.isFiringProjectiles = tag.getBoolean("IsFiringProjectiles");
		}
	}
}
