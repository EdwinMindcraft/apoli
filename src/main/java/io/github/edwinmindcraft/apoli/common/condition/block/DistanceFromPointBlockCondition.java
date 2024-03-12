package io.github.edwinmindcraft.apoli.common.condition.block;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import io.github.edwinmindcraft.apoli.common.action.configuration.DistanceFromPointConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;


public class DistanceFromPointBlockCondition extends BlockCondition<DistanceFromPointConfiguration> {
	public DistanceFromPointBlockCondition(Codec<DistanceFromPointConfiguration> codec) {
		super(codec);
	}

	@Override
	protected boolean check(DistanceFromPointConfiguration configuration, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter) {
		return reader instanceof Level level ? configuration.test(null, Vec3.atLowerCornerOf(position), level) : configuration.comparison().check(Double.POSITIVE_INFINITY);
	}
}
