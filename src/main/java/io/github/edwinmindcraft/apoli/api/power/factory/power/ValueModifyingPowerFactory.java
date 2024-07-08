package io.github.edwinmindcraft.apoli.api.power.factory.power;

import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.apoli.api.power.IValueModifyingPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IValueModifyingPowerConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import net.minecraft.world.entity.Entity;

import java.util.List;

public abstract class ValueModifyingPowerFactory<T extends IValueModifyingPowerConfiguration> extends PowerFactory<T> implements IValueModifyingPower<T> {
	protected ValueModifyingPowerFactory(MapCodec<T> codec) {
		super(codec);
	}

	@Override
	public List<ConfiguredModifier<?>> getModifiers(ConfiguredPower<T, ?> configuration, Entity player) {
		return configuration.getConfiguration().modifiers().getContent();
	}
}
