package io.github.edwinmindcraft.apoli.api.power;

import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public interface INightVisionPower<T extends IDynamicFeatureConfiguration> {
	@SuppressWarnings({"rawtypes", "unchecked"})
	static Optional<Float> getNightVisionStrength(@Nullable Entity player) {
		return PowerContainer.get(player).map(x -> x.getPowers().stream()).orElseGet(Stream::of)
				.filter(x -> x.value().isActive(Objects.requireNonNull(player)) && x.value().getFactory() instanceof INightVisionPower)
				.map(x -> getValue((ConfiguredPower) x.value(), player))
				.max(Float::compareTo);
	}

	private static <T extends IDynamicFeatureConfiguration, F extends PowerFactory<T> & INightVisionPower<T>> float getValue(ConfiguredPower<T, F> configuration, Entity player) {
		return configuration.getFactory().getStrength(configuration, player);
	}

	float getStrength(ConfiguredPower<T, ?> configuration, Entity player);
}
