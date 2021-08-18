package dev.experimental.apoli.common.action.block;

import dev.experimental.apoli.api.power.factory.BlockAction;
import dev.experimental.apoli.common.action.configuration.BlockConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class AddBlockAction extends BlockAction<BlockConfiguration> {

	public AddBlockAction() {
		super(BlockConfiguration.codec("block"));
	}

	@Override
	public void execute(BlockConfiguration configuration, Level world, BlockPos pos, Direction direction) {
		world.setBlockAndUpdate(pos.relative(direction), configuration.block().defaultBlockState());
	}
}
