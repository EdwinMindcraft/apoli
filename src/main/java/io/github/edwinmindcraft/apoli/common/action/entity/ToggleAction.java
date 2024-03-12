package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.PowerReference;
import io.github.edwinmindcraft.apoli.api.power.ITogglePower;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class ToggleAction extends EntityAction<PowerReference> {
	public ToggleAction() {
		super(PowerReference.codec("power"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void execute(PowerReference configuration, Entity entity) {
		if (entity instanceof Player player) {
			ConfiguredPower<?, ?> power = PowerContainer.get(player).resolve()
					.map(x -> x.getPower(configuration.power())).map(Holder::value).orElse(null);
			if (power.getFactory() instanceof ITogglePower cp)
				cp.toggle(power, player);
		}
	}
}
