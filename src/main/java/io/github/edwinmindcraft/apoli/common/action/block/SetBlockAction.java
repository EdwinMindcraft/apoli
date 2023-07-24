package io.github.edwinmindcraft.apoli.common.action.block;

import io.github.edwinmindcraft.apoli.api.power.factory.BlockAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.BlockStateConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class SetBlockAction extends BlockAction<BlockStateConfiguration> {

	public SetBlockAction() {
		super(BlockStateConfiguration.codec("block"));
	}

	@Override
	public void execute(BlockStateConfiguration configuration, Level world, BlockPos pos, Direction direction) {
		world.setBlockAndUpdate(pos, configuration.state());
	}
}
