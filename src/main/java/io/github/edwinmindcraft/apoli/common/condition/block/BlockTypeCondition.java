package io.github.edwinmindcraft.apoli.common.condition.block;

import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import io.github.edwinmindcraft.apoli.common.action.configuration.BlockConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;


public class BlockTypeCondition extends BlockCondition<BlockConfiguration> {

	public BlockTypeCondition() {
		super(BlockConfiguration.codec("block"));
	}

	@Override
	protected boolean check(BlockConfiguration configuration, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter) {
		return stateGetter.get().is(configuration.block());
	}
}
