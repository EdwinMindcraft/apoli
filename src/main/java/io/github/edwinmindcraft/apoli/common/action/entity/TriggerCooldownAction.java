package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.PowerReference;
import io.github.edwinmindcraft.apoli.api.power.ICooldownPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class TriggerCooldownAction extends EntityAction<PowerReference> {
	public TriggerCooldownAction() {
		super(PowerReference.codec("power"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void execute(PowerReference configuration, Entity entity) {
		if (entity instanceof LivingEntity living) {
			ConfiguredPower<?, ?> power = PowerContainer.get(living).resolve()
					.map(x -> x.getPower(configuration.power())).map(Holder::value).orElse(null);
			if (power != null && power.getFactory() instanceof ICooldownPower cp)
				cp.use(power, living);
		}
	}
}
