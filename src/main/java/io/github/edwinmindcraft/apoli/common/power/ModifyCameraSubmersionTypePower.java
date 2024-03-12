package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyFogTypeConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;

import java.util.Optional;

import static io.github.edwinmindcraft.apoli.common.registry.ApoliPowers.MODIFY_CAMERA_SUBMERSION;

public class ModifyCameraSubmersionTypePower extends PowerFactory<ModifyFogTypeConfiguration> {
	public static Optional<FogType> tryReplace(Entity entity, FogType original) {
		return PowerContainer.getPowers(entity, MODIFY_CAMERA_SUBMERSION.get()).stream().flatMap(x -> x.value().getFactory().tryReplace(x.value(), entity, original).stream()).findFirst();
	}

	public ModifyCameraSubmersionTypePower() {
		super(ModifyFogTypeConfiguration.CODEC);
	}

	public Optional<FogType> tryReplace(ConfiguredPower<ModifyFogTypeConfiguration, ?> power, Entity entity, FogType original) {
		ModifyFogTypeConfiguration config = power.getConfiguration();
		if (config.from().isEmpty())
			return Optional.of(config.to());
		return config.from().filter(original::equals).map(k -> config.to());
	}
}
