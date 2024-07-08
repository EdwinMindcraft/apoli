package io.github.edwinmindcraft.apoli.common.condition.block;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.AdjacentConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;


import java.util.Arrays;
import java.util.function.Supplier;

public class AdjacentCondition extends BlockCondition<AdjacentConfiguration> {
	public AdjacentCondition() {
		super(AdjacentConfiguration.CODEC);
	}

	@Override
	protected boolean check(AdjacentConfiguration configuration, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter) {
		int count = Math.toIntExact(Arrays.stream(Direction.values())
				.filter(x -> ConfiguredBlockCondition.check(configuration.condition(), reader, position.relative(x))).count());
		return configuration.comparison().check(count);
	}
}
