package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.BiEntityConditionConfiguration;
import net.minecraft.world.entity.Entity;

public class BiEntityConditionPower extends PowerFactory<BiEntityConditionConfiguration> {
	public static boolean any(BiEntityConditionPower power, Entity holder, Entity actor, Entity target) {
		return PowerContainer.getPowers(holder, power).stream().anyMatch(x -> ConfiguredBiEntityCondition.check(x.value().getConfiguration().biEntityCondition(), actor, target));
	}

	public BiEntityConditionPower() {
		super(BiEntityConditionConfiguration.CODEC);
	}
}
