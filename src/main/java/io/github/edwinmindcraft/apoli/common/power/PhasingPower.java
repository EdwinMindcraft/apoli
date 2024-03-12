package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.PhasingConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;


import java.util.Optional;

public class PhasingPower extends PowerFactory<PhasingConfiguration> {
	public static boolean shouldPhaseThrough(Entity entity, LevelReader reader, BlockPos pos, Supplier<@NotNull BlockState> stateGetter, boolean isAbove) {
		return PowerContainer.getPowers(entity, ApoliPowers.PHASING.get()).stream().anyMatch(x -> (!isAbove || x.value().getConfiguration().canPhaseDown(entity)) && x.value().getConfiguration().canPhaseThrough(reader, pos, stateGetter));
	}

	public static boolean shouldPhaseThrough(Entity entity, LevelReader reader, BlockPos pos, Supplier<@NotNull BlockState> stateGetter) {
		return shouldPhaseThrough(entity, reader, pos, stateGetter, false);
	}

	public static boolean shouldPhaseThrough(Entity entity, BlockPos pos) {
		return shouldPhaseThrough(entity, entity.level(), pos, () -> entity.level().getBlockState(pos));
	}

	public static boolean hasRenderMethod(Entity entity, PhasingConfiguration.RenderType renderType) {
		return PowerContainer.getPowers(entity, ApoliPowers.PHASING.get()).stream().anyMatch(x -> renderType.equals(x.value().getConfiguration().renderType()));
	}

	public static Optional<Float> getRenderMethod(Entity entity, PhasingConfiguration.RenderType renderType) {
		return PowerContainer.getPowers(entity, ApoliPowers.PHASING.get()).stream().filter(x -> renderType.equals(x.value().getConfiguration().renderType())).map(x -> x.value().getConfiguration().viewDistance()).min(Float::compareTo);
	}


	public PhasingPower() {
		super(PhasingConfiguration.CODEC);
	}
}
