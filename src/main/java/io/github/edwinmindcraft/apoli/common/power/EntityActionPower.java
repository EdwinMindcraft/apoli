package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.EntityActionConfiguration;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class EntityActionPower extends PowerFactory<EntityActionConfiguration> {
	/**
	 * Executes all powers for the given factory on the given entity.
	 *
	 * @param power  The {@link PowerFactory} to execute.
	 * @param entity The entity to execute the powers on.
	 *
	 * @return {@code true} if any power exists, {@code false} otherwise.
	 */
	public static boolean execute(EntityActionPower power, Entity entity) {
		List<Holder<ConfiguredPower<EntityActionConfiguration, EntityActionPower>>> powers = PowerContainer.getPowers(entity, power);
		powers.forEach(x -> ConfiguredEntityAction.execute(x.value().getConfiguration().entityAction(), entity));
		return !powers.isEmpty();
	}

	public EntityActionPower() {
		super(EntityActionConfiguration.CODEC);
	}
}
