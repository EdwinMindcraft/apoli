package io.github.edwinmindcraft.apoli.common.condition.block;

import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.context.BlockConditionContext;
import io.github.edwinmindcraft.apoli.common.condition.meta.IDelegatedConditionConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;


public class DelegatedBlockCondition<T extends IDelegatedConditionConfiguration<BlockConditionContext>> extends BlockCondition<T> {
	public DelegatedBlockCondition(MapCodec<T> codec) {
		super(codec);
	}

	@Override
	protected boolean check(T configuration, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter) {
		return configuration.check(new BlockConditionContext(reader, position, stateGetter));
	}
}
