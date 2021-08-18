package dev.experimental.apoli.common.action.entity;

import dev.experimental.apoli.api.ApoliAPI;
import dev.experimental.apoli.api.power.configuration.ConfiguredPower;
import dev.experimental.apoli.api.power.factory.EntityAction;
import dev.experimental.apoli.common.action.configuration.ChangeResourceConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class ChangeResourceAction extends EntityAction<ChangeResourceConfiguration> {
	public ChangeResourceAction() {
		super(ChangeResourceConfiguration.CODEC);
	}

	@Override
	public void execute(ChangeResourceConfiguration configuration, Entity entity) {
		if (entity instanceof Player player) {
			ConfiguredPower<?, ?> power = ApoliAPI.getPowerContainer(player).getPower(configuration.resource());
			if (power != null) {
				power.change(player, configuration.amount());
				ApoliAPI.synchronizePowerContainer(player);
			}
		}
	}
}
