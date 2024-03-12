package io.github.edwinmindcraft.apoli.common.condition.block;

import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredFluidCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;


public class FluidBlockCondition extends BlockCondition<FieldConfiguration<ConfiguredFluidCondition<?, ?>>> {

	public FluidBlockCondition() {
		super(FieldConfiguration.codec(ConfiguredFluidCondition.CODEC, "fluid_condition"));
	}

	@Override
	protected boolean check(FieldConfiguration<ConfiguredFluidCondition<?, ?>> configuration, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter) {
		return configuration.value().check(stateGetter.get().getFluidState());
	}
}
