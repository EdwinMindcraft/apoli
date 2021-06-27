package dev.experimental.apoli.common.condition.entity;

import dev.experimental.apoli.api.power.factory.EntityCondition;
import dev.experimental.apoli.common.condition.configuration.BlockInRadiusConfiguration;
import io.github.apace100.apoli.util.Shape;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class BlockInRadiusCondition extends EntityCondition<BlockInRadiusConfiguration> {

	public BlockInRadiusCondition() {
		super(BlockInRadiusConfiguration.CODEC);
	}

	@Override
	public boolean check(BlockInRadiusConfiguration configuration, LivingEntity entity) {
		int count = 0;
		int stopAt = configuration.comparison().getOptimalStoppingPoint();
		for (BlockPos pos : Shape.getPositions(entity.getBlockPos(), configuration.shape(), configuration.radius())) {
			if (configuration.blockCondition().check(new CachedBlockPosition(entity.world, pos, true)))
				if (++count == stopAt) break;
		}
		return configuration.comparison().check(count);
	}
}
