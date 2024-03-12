package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ClimbingConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ClimbingPower extends PowerFactory<ClimbingConfiguration> {
	public static boolean check(LivingEntity player, Consumer<BlockPos> climbingPosSetter) {
		List<ConfiguredPower<ClimbingConfiguration, ClimbingPower>> climbingPowers = PowerContainer.get(player)
				.map(x -> x.getPowers(ApoliPowers.CLIMBING.get(), true).stream().map(Holder::value))
				.orElseGet(Stream::of).toList();
		if (climbingPowers.size() > 0) {
			if (climbingPowers.stream().anyMatch(x -> x.isActive(player))) {
				climbingPosSetter.accept(player.blockPosition());
				return true;
			}
			return player.isSuppressingSlidingDownLadder() && climbingPowers.stream().anyMatch(x -> x.getFactory().canHold(x, player));
		}
		return false;
	}

	public ClimbingPower() {
		super(ClimbingConfiguration.CODEC);
	}

	public boolean canHold(ConfiguredPower<ClimbingConfiguration, ?> configuration, Entity player) {
		Holder<ConfiguredEntityCondition<?, ?>> holdingCondition = configuration.getConfiguration().condition();
		return configuration.getConfiguration().allowHolding() && (holdingCondition == null || !holdingCondition.isBound() ? configuration.isActive(player) : holdingCondition.value().check(player));
	}
}
