package io.github.edwinmindcraft.apoli.common.condition.block;

import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.LightLevelConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;


public class LightLevelCondition extends BlockCondition<LightLevelConfiguration> {

	public LightLevelCondition() {
		super(LightLevelConfiguration.CODEC);
	}

	@Override
	protected boolean check(LightLevelConfiguration configuration, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter) {
		return configuration.comparison().check(configuration.getLightLevel(reader, position));
	}
}
