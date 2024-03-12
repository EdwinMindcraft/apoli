package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.InteractionPowerConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.BlockInteractionConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class ActionOnBlockUsePower extends PowerFactory<BlockInteractionConfiguration> {

	public static Optional<InteractionResult> tryInteract(Entity entity, BlockPos pos, Direction direction, InteractionHand hand) {
		return PowerContainer.getPowers(entity, ApoliPowers.ACTION_ON_BLOCK_USE.get()).stream().flatMap(x -> x.value().getFactory().tryExecute(x.value(), entity, pos, direction, hand).stream()).reduce(InteractionPowerConfiguration::reduce);
	}

	public ActionOnBlockUsePower() {
		super(BlockInteractionConfiguration.CODEC);
	}

	public Optional<InteractionResult> tryExecute(ConfiguredPower<BlockInteractionConfiguration, ?> configuration, Entity entity, BlockPos pos, Direction direction, InteractionHand hand) {
		if (entity instanceof LivingEntity living && configuration.getConfiguration().check(entity.level(), pos, direction, hand, living.getItemInHand(hand)))
			return Optional.of(configuration.getConfiguration().executeAction(entity, pos, direction, hand));
		return Optional.empty();
	}
}
