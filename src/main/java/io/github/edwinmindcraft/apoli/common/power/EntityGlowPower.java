package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ColorConfiguration;
import io.github.edwinmindcraft.apoli.common.power.configuration.EntityGlowConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.stream.Stream;

public class EntityGlowPower extends PowerFactory<EntityGlowConfiguration> {

	private static Stream<Holder<ConfiguredPower<EntityGlowConfiguration, EntityGlowPower>>> getGlowPowers(Entity actor, Entity target, boolean isPlayer) {
		var selfGlow = PowerContainer.getPowers(target, ApoliPowers.SELF_GLOW.get());
		if (isPlayer)
			return selfGlow.stream();
		var entityGlow = PowerContainer.getPowers(actor, ApoliPowers.ENTITY_GLOW.get());
		return Stream.concat(
				entityGlow.stream(),
				selfGlow.stream()
		);
	}

	public static boolean shouldGlow(Entity actor, Entity target, boolean isPlayer) {
		return getGlowPowers(actor, target, isPlayer).anyMatch(x -> x.value().getFactory().doesApply(x.value().getConfiguration(), actor, target));
	}

	public static Optional<ColorConfiguration> getGlowColor(Entity actor, Entity target) {
		return getGlowPowers(actor, target, false).flatMap(x -> x.value().getFactory().getColor(x.value().getConfiguration(), actor, target).stream()).findFirst();
	}

	private final boolean targetSelf;

	public EntityGlowPower(boolean targetSelf) {
		super(EntityGlowConfiguration.CODEC);
		this.targetSelf = targetSelf;
	}

	public boolean doesApply(EntityGlowConfiguration configuration, Entity actor, Entity target) {
		return configuration.applyChecks(actor, target, this.targetSelf);
	}

	public Optional<ColorConfiguration> getColor(EntityGlowConfiguration configuration, Entity actor, Entity target) {
		if (this.doesApply(configuration, actor, target) && !configuration.useTeams())
			return Optional.of(configuration.color());
		return Optional.empty();
	}
}
