package io.github.edwinmindcraft.apoli.common.condition.block;

import io.github.edwinmindcraft.apoli.api.configuration.IntegerComparisonConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;


public class HeightCondition extends BlockCondition<IntegerComparisonConfiguration> {
	public HeightCondition() {
		super(IntegerComparisonConfiguration.CODEC);
	}

	@Override
	protected boolean check(IntegerComparisonConfiguration configuration, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter) {
		return configuration.check(position.getY());
	}
}
