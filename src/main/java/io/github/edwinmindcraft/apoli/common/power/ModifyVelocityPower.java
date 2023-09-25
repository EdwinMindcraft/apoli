package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.power.IValueModifyingPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyVelocityConfiguration;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class ModifyVelocityPower extends PowerFactory<ModifyVelocityConfiguration> implements IValueModifyingPower<ModifyVelocityConfiguration> {

	public ModifyVelocityPower() {
		super(ModifyVelocityConfiguration.CODEC);
	}

	@Override
	public List<ConfiguredModifier<?>> getModifiers(ConfiguredPower<ModifyVelocityConfiguration, ?> configuration, Entity player) {
		return configuration.getConfiguration().modifiers().entries();
	}
}

