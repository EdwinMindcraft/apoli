package io.github.edwinmindcraft.apoli.api.power.factory.power;

import com.mojang.serialization.Codec;
import io.github.apace100.apoli.util.HudRender;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.IHudRenderedPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IHudRenderedVariableIntPowerConfiguration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class HudRenderedVariableIntPowerFactory<T extends IHudRenderedVariableIntPowerConfiguration> extends VariableIntPowerFactory<T> implements IHudRenderedPower<T> {
	protected HudRenderedVariableIntPowerFactory(Codec<T> codec) {
		super(codec);
	}

	protected HudRenderedVariableIntPowerFactory(Codec<T> codec, boolean allowConditions) {
		super(codec, allowConditions);
	}

	@Override
	public HudRender getRenderSettings(ConfiguredPower<T, ?> configuration, Entity player) {
		return configuration.getConfiguration().hudRender();
	}

	@Override
	public float getFill(ConfiguredPower<T, ?> configuration, Entity player) {
		return this.getProgress(configuration, player);
	}

	@Override
	public boolean shouldRender(ConfiguredPower<T, ?> configuration, Entity player) {
		return true;
	}

	public static abstract class Simple<T extends IHudRenderedVariableIntPowerConfiguration> extends HudRenderedVariableIntPowerFactory<T> {
		protected Simple(Codec<T> codec) {
			super(codec);
		}

		protected Simple(Codec<T> codec, boolean allowConditions) {
			super(codec, allowConditions);
		}

		protected AtomicInteger getCurrentValue(ConfiguredPower<T, ?> configuration, PowerContainer container) {
			return configuration.getPowerData(container, () -> new AtomicInteger(configuration.getConfiguration().initialValue()));
		}

		@Override
		protected int get(ConfiguredPower<T, ?> configuration, @Nullable PowerContainer container) {
			if (container == null)
				return configuration.getConfiguration().initialValue();
			return this.getCurrentValue(configuration, container).get();
		}

		@Override
		protected void set(ConfiguredPower<T, ?> configuration, @Nullable PowerContainer container, int value) {
			if (container == null)
				return;
			this.getCurrentValue(configuration, container).set(value);
		}

		@Override
		public void serialize(ConfiguredPower<T, ?> configuration, PowerContainer container, CompoundTag tag) {
			tag.putInt("Value", this.get(configuration, container));
		}

		@Override
		public void deserialize(ConfiguredPower<T, ?> configuration, PowerContainer container, CompoundTag tag) {
			this.set(configuration, container, tag.getInt("Value"));
		}
	}
}
