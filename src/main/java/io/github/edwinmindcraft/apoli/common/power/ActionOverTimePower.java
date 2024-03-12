package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ActionOverTimeConfiguration;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActionOverTimePower extends PowerFactory<ActionOverTimeConfiguration> {
	public ActionOverTimePower() {
		super(ActionOverTimeConfiguration.CODEC);
		this.ticking(true);
	}

	@Override
	public void tick(ConfiguredPower<ActionOverTimeConfiguration, ?> configuration, Entity player) {
		//TODO Start ticking after the power is added.
		AtomicBoolean data = configuration.getPowerData(player, () -> new AtomicBoolean(false));
		ActionOverTimeConfiguration config = configuration.getConfiguration();
		if (configuration.isActive(player)) {
			if (!data.get())
				ConfiguredEntityAction.execute(config.risingAction(), player);
			ConfiguredEntityAction.execute(config.entityAction(), player);
			data.set(true);
		} else {
			if (data.get())
				ConfiguredEntityAction.execute(config.fallingAction(), player);
			data.set(false);
		}
	}

	@Override
	protected int tickInterval(ActionOverTimeConfiguration configuration, Entity player) {
		return configuration.interval();
	}

	@Override
	public void serialize(ConfiguredPower<ActionOverTimeConfiguration, ?> configuration, PowerContainer container, CompoundTag tag) {
		tag.putBoolean("WasActive", configuration.getPowerData(container, () -> new AtomicBoolean(false)).get());
	}

	@Override
	public void deserialize(ConfiguredPower<ActionOverTimeConfiguration, ?> configuration, PowerContainer container, CompoundTag tag) {
		AtomicBoolean data = configuration.getPowerData(container, () -> new AtomicBoolean(false));

		data.set(!Objects.equals(tag.get("WasActive"), ByteTag.ZERO));
	}
}
