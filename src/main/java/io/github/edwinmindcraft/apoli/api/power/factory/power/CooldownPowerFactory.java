package io.github.edwinmindcraft.apoli.api.power.factory.power;

import com.mojang.serialization.Codec;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.util.HudRender;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.ICooldownPower;
import io.github.edwinmindcraft.apoli.api.power.IHudRenderedPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.ICooldownPowerConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.lang3.mutable.MutableLong;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicLong;

public abstract class CooldownPowerFactory<T extends ICooldownPowerConfiguration> extends PowerFactory<T> implements ICooldownPower<T>, IHudRenderedPower<T> {
	protected CooldownPowerFactory(Codec<T> codec) {
		super(codec);
	}

	protected CooldownPowerFactory(Codec<T> codec, boolean allowConditions) {
		super(codec, allowConditions);
	}

	@Override
	public void use(ConfiguredPower<T, ?> configuration, LivingEntity entity) {
		this.setLastUseTime(configuration, entity, entity.getCommandSenderWorld().getGameTime());
		IPowerContainer.sync(entity);
	}

	@Override
	public void onGained(ConfiguredPower<T, ?> configuration, LivingEntity entity) {
		this.setLastUseTime(configuration, entity, entity.getCommandSenderWorld().getGameTime());
	}

	@Override
	public boolean canUse(ConfiguredPower<T, ?> configuration, LivingEntity entity) {
		return this.getRemainingDuration(configuration, entity) <= 0 && configuration.isActive(entity);
	}

	@Override
	public HudRender getRenderSettings(ConfiguredPower<T, ?> configuration, LivingEntity entity) {
		return configuration.getConfiguration().hudRender();
	}

	@Override
	public float getFill(ConfiguredPower<T, ?> configuration, LivingEntity entity) {
		return this.getProgress(configuration, entity);
	}

	@Override
	public boolean shouldRender(ConfiguredPower<T, ?> configuration, LivingEntity entity) {
		return this.getRemainingDuration(configuration, entity) > 0;
	}

	protected long getLastUseTime(ConfiguredPower<T, ?> configuration, LivingEntity entity) {
		return this.getLastUseTime(configuration, entity, IPowerContainer.get(entity).resolve().orElse(null));
	}

	protected abstract long getLastUseTime(ConfiguredPower<T, ?> configuration, LivingEntity entity, @Nullable IPowerContainer container);

	protected void setLastUseTime(ConfiguredPower<T, ?> configuration, LivingEntity entity, long value) {
		this.setLastUseTime(configuration, entity, IPowerContainer.get(entity).resolve().orElse(null), value);
	}

	protected abstract void setLastUseTime(ConfiguredPower<T, ?> configuration, LivingEntity entity, @Nullable IPowerContainer container, long value);

	protected long getElapsedDuration(ConfiguredPower<T, ?> configuration, LivingEntity entity) {
		return Math.max(entity.getCommandSenderWorld().getGameTime() - this.getLastUseTime(configuration, entity), 0);
	}

	protected long getRemainingDuration(ConfiguredPower<T, ?> configuration, LivingEntity entity) {
		return Math.max(this.getLastUseTime(configuration, entity) + configuration.getConfiguration().duration() - entity.getCommandSenderWorld().getGameTime(), 0);
	}

	@Override
	public int assign(ConfiguredPower<T, ?> configuration, LivingEntity entity, int value) {
		value = Mth.clamp(value, this.getMaximum(configuration, entity), this.getMaximum(configuration, entity));
		this.setLastUseTime(configuration, entity, entity.getCommandSenderWorld().getGameTime() - value);
		return value;
	}

	@Override
	public int getValue(ConfiguredPower<T, ?> configuration, LivingEntity entity) {
		return Math.toIntExact(Mth.clamp(this.getElapsedDuration(configuration, entity), this.getMinimum(configuration, entity), this.getMaximum(configuration, entity)));
	}

	@Override
	public int getMaximum(ConfiguredPower<T, ?> configuration, LivingEntity entity) {
		return configuration.getConfiguration().duration();
	}

	@Override
	public int getMinimum(ConfiguredPower<T, ?> configuration, LivingEntity entity) {
		return 0;
	}

	/**
	 * A partial implementation of {@link CooldownPowerFactory} with default serialization functions.
	 */
	public static abstract class Simple<T extends ICooldownPowerConfiguration> extends CooldownPowerFactory<T> {
		protected Simple(Codec<T> codec) {
			super(codec);
		}

		protected Simple(Codec<T> codec, boolean allowConditions) {
			super(codec, allowConditions);
		}

		protected MutableLong getUseTime(ConfiguredPower<T, ?> configuration, IPowerContainer container) {
			return configuration.getPowerData(container, () -> new MutableLong(0));
		}

		@Override
		protected long getLastUseTime(ConfiguredPower<T, ?> configuration, LivingEntity entity, @Nullable IPowerContainer container) {
			if (container == null)
				return 0;
			return this.getUseTime(configuration, container).getValue();
		}

		@Override
		protected void setLastUseTime(ConfiguredPower<T, ?> configuration, LivingEntity entity, @Nullable IPowerContainer container, long value) {
			if (container == null)
				return;
			this.getUseTime(configuration, container).setValue(value);
		}

		@Override
		public Tag serialize(ConfiguredPower<T, ?> configuration, LivingEntity entity, IPowerContainer container) {
			return LongTag.valueOf(this.getLastUseTime(configuration, entity, container));
		}

		@Override
		public void deserialize(ConfiguredPower<T, ?> configuration, LivingEntity entity, IPowerContainer container, Tag tag) {
			if (tag instanceof LongTag longTag)
				this.setLastUseTime(configuration, entity, container, longTag.getAsLong());
		}
	}
}
