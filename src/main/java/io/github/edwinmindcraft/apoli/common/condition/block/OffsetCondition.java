package io.github.edwinmindcraft.apoli.common.condition.block;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import io.github.edwinmindcraft.apoli.common.action.configuration.OffsetConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;


public class OffsetCondition extends BlockCondition<OffsetConfiguration<ConfiguredBlockCondition<?, ?>>> {

	public OffsetCondition() {
		super(OffsetConfiguration.codec(ConfiguredBlockCondition.required("condition")));
	}

	@Override
	protected boolean check(OffsetConfiguration<ConfiguredBlockCondition<?, ?>> configuration, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter) {
		BlockPos target = position.offset(configuration.asBlockPos());
		return ConfiguredBlockCondition.check(configuration.value(), reader, target, () -> reader.getBlockState(target));
	}
}
