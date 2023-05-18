package io.github.edwinmindcraft.apoli.common.action.entity;

import com.mojang.serialization.Codec;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.ChangeResourceConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ChangeResourceAction extends EntityAction<ChangeResourceConfiguration> {
	public ChangeResourceAction(Codec<ChangeResourceConfiguration> codec) {
		super(codec);
	}

	@Override
	public void execute(ChangeResourceConfiguration configuration, Entity entity) {
		if (entity instanceof LivingEntity living && configuration.resource().isBound()) {
			ConfiguredPower<?, ?> power = configuration.resource().value();
			if (IPowerContainer.get(entity).map(x -> x.hasPower(power.getRegistryName())).orElse(false)) {
				if (configuration.operation() == ResourceOperation.ADD)
					power.change(living, configuration.amount());
				else if (configuration.operation() == ResourceOperation.SET)
					power.assign(living, configuration.amount());
				ApoliAPI.synchronizePowerContainer(living);
			}
		}
	}
}
