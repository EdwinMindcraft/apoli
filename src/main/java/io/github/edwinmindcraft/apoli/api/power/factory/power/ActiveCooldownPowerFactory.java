package io.github.edwinmindcraft.apoli.api.power.factory.power;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IActiveCooldownPowerConfiguration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicLong;

public abstract class ActiveCooldownPowerFactory<T extends IActiveCooldownPowerConfiguration> extends CooldownPowerFactory<T> implements IActivePower<T> {
	protected ActiveCooldownPowerFactory(Codec<T> codec) {
		super(codec);
	}

	protected ActiveCooldownPowerFactory(Codec<T> codec, boolean allowConditions) {
		super(codec, allowConditions);
	}

	protected abstract void execute(ConfiguredPower<T, ?> configuration, Entity player);

	@Override
	public void activate(ConfiguredPower<T, ?> configuration, Entity player) {
		if (this.canUse(configuration, player)) {
			this.execute(configuration, player);
			this.use(configuration, player);
		}
	}

	@Override
	public Key getKey(ConfiguredPower<T, ?> configuration, @Nullable Entity player) {
		return configuration.getConfiguration().key();
	}

	/**
	 * A partial implementation of {@link ActiveCooldownPowerFactory} with default serialization functions.
	 */
	public static abstract class Simple<T extends IActiveCooldownPowerConfiguration> extends ActiveCooldownPowerFactory<T> {
		protected Simple(Codec<T> codec) {
			super(codec);
		}

		protected Simple(Codec<T> codec, boolean allowConditions) {
			super(codec, allowConditions);
		}

		protected AtomicLong getUseTime(ConfiguredPower<T, ?> configuration, PowerContainer container) {
			return configuration.getPowerData(container, () -> new AtomicLong(Long.MIN_VALUE));
		}

		@Override
		protected long getLastUseTime(ConfiguredPower<T, ?> configuration, @Nullable PowerContainer container) {
			if (container == null)
				return 0;
			return this.getUseTime(configuration, container).get();
		}

		@Override
		protected void setLastUseTime(ConfiguredPower<T, ?> configuration, @Nullable PowerContainer container, long value) {
			if (container == null) return;
			this.getUseTime(configuration, container).set(value);
		}

		@Override
		public void serialize(ConfiguredPower<T, ?> configuration, PowerContainer container, CompoundTag tag) {
			tag.putLong("LastUseTime", this.getLastUseTime(configuration, container));
		}

		@Override
		public void deserialize(ConfiguredPower<T, ?> configuration, PowerContainer container, CompoundTag tag) {
			this.setLastUseTime(configuration, container, tag.getLong("LastUseTime"));
		}
	}
}
