package io.github.edwinmindcraft.apoli.common.condition.block;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;


import java.util.Arrays;

public class AttachableCondition extends BlockCondition<NoConfiguration> {

	public static final Codec<AttachableCondition> CODEC = Codec.unit(new AttachableCondition());

	public AttachableCondition() {
		super(NoConfiguration.CODEC);
	}

	@Override
	protected boolean check(NoConfiguration configuration, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter) {
		return Arrays.stream(Direction.values()).anyMatch(d -> reader.getBlockState(position.relative(d)).isFaceSturdy(reader, position, d.getOpposite()));
	}
}
