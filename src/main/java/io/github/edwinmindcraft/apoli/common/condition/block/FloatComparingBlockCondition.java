package io.github.edwinmindcraft.apoli.common.condition.block;

import io.github.edwinmindcraft.apoli.api.configuration.FloatComparisonConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;


public class FloatComparingBlockCondition extends BlockCondition<FloatComparisonConfiguration> {
	private final BlockFloatFunction function;

	public FloatComparingBlockCondition(BlockFloatFunction function) {
		super(FloatComparisonConfiguration.CODEC);
		this.function = function;
	}

	@Override
	protected boolean check(FloatComparisonConfiguration configuration, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter) {
		float apply = this.function.apply(reader, position, stateGetter);
		return !Float.isNaN(apply) && configuration.check(apply);
	}
}
