package io.github.edwinmindcraft.apoli.api.power.factory.power;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.ITogglePower;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.TogglePowerConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class TogglePowerFactory<T extends TogglePowerConfiguration> extends PowerFactory<T> implements ITogglePower<T> {
	protected TogglePowerFactory(Codec<T> codec) {
		super(codec);
	}

	protected TogglePowerFactory(Codec<T> codec, boolean allowConditions) {
		super(codec, allowConditions);
	}

	protected void setStatus(ConfiguredPower<T, ?> configuration, Entity entity, boolean status) {
		this.setStatus(configuration, PowerContainer.get(entity).resolve().orElse(null), status);
	}

	protected boolean getStatus(ConfiguredPower<T, ?> configuration, Entity entity) {
		return this.getStatus(configuration, PowerContainer.get(entity).resolve().orElse(null));
	}

	protected abstract void setStatus(ConfiguredPower<T, ?> configuration, @Nullable PowerContainer container, boolean status);

	protected abstract boolean getStatus(ConfiguredPower<T, ?> configuration, @Nullable PowerContainer container);

	@Override
	public boolean isActive(ConfiguredPower<T, ?> configuration, Entity player) {
		return super.isActive(configuration, player) && this.getStatus(configuration, player);
	}

	@Override
	protected boolean shouldTickWhenActive(ConfiguredPower<T, ?> configuration, Entity entity) {
		return !configuration.getConfiguration().retainState() && configuration.getData().conditions().size() > 0;
	}

	@Override
	protected boolean shouldTickWhenInactive(ConfiguredPower<T, ?> configuration, Entity entity) {
		return true;
	}

	@Override
	public void tick(ConfiguredPower<T, ?> configuration, Entity entity) {
		if (!super.isActive(configuration, entity) && this.getStatus(configuration, entity)) {
			this.setStatus(configuration, entity, false);
			PowerContainer.sync(entity);
		}
	}

	@Override
	public void activate(ConfiguredPower<T, ?> configuration, Entity player) {
		this.toggle(configuration, player);
	}

	@Override
	public void toggle(ConfiguredPower<T, ?> configuration, Entity entity) {
		this.setStatus(configuration, entity, !this.getStatus(configuration, entity));
		PowerContainer.sync(entity);
	}

	@Override
	public Key getKey(ConfiguredPower<T, ?> configuration, @Nullable Entity player) {
		return configuration.getConfiguration().key();
	}

	public static abstract class Simple<T extends TogglePowerConfiguration> extends TogglePowerFactory<T> {
		protected Simple(Codec<T> codec) {
			super(codec);
		}

		protected Simple(Codec<T> codec, boolean allowConditions) {
			super(codec, allowConditions);
		}

		private AtomicBoolean getData(ConfiguredPower<T, ?> configuration, PowerContainer container) {
			return configuration.getPowerData(container, () -> new AtomicBoolean(configuration.getConfiguration().defaultState()));
		}

		@Override
		protected void setStatus(ConfiguredPower<T, ?> configuration, @Nullable PowerContainer container, boolean status) {
			if (container == null)
				return;
			this.getData(configuration, container).set(status);
		}

		@Override
		protected boolean getStatus(ConfiguredPower<T, ?> configuration, @Nullable PowerContainer container) {
			if (container == null)
				return false;
			return this.getData(configuration, container).get();
		}

		@Override
		public void serialize(ConfiguredPower<T, ?> configuration, PowerContainer container, CompoundTag tag) {
			tag.putBoolean("Status", this.getStatus(configuration, container));
		}

		@Override
		public void deserialize(ConfiguredPower<T, ?> configuration, PowerContainer container, CompoundTag tag) {
			this.setStatus(configuration, container, tag.getBoolean("Status"));
		}
	}
}
