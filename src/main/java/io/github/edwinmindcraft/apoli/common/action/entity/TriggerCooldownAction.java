package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.PowerReference;
import io.github.edwinmindcraft.apoli.api.power.ICooldownPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class TriggerCooldownAction extends EntityAction<PowerReference> {
	public TriggerCooldownAction() {
		super(PowerReference.codec("power"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void execute(PowerReference configuration, Entity entity) {
		if (entity instanceof LivingEntity living) {
			ConfiguredPower<?, ?> power = IPowerContainer.get(living)
					.resolve()
					.map(x -> x.getPower(configuration.power())).orElse(null);
			if (power.getFactory() instanceof ICooldownPower cp)
				cp.use(power, living);
		}
	}
}
