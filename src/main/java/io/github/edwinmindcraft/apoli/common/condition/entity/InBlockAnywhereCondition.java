package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.InBlockAnywhereConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public class InBlockAnywhereCondition extends EntityCondition<InBlockAnywhereConfiguration> {

	public InBlockAnywhereCondition() {
		super(InBlockAnywhereConfiguration.CODEC);
	}

	@Override
	public boolean check(InBlockAnywhereConfiguration configuration, Entity entity) {
		int stopAt = configuration.comparison().getOptimalStoppingPoint();
		int count = 0;
		AABB box = entity.getBoundingBox();
		BlockPos blockPos = new BlockPos((int)(box.minX + 0.001D), (int)(box.minY + 0.001D), (int)(box.minZ + 0.001D));
		BlockPos blockPos2 = new BlockPos((int)(box.maxX - 0.001D), (int)Math.min(box.maxY - 0.001D, entity.level().getHeight()), (int)(box.maxZ - 0.001D));
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		for (int i = blockPos.getX(); i <= blockPos2.getX() && count < stopAt; ++i) {
			for (int j = blockPos.getY(); j <= blockPos2.getY() && count < stopAt; ++j) {
				for (int k = blockPos.getZ(); k <= blockPos2.getZ() && count < stopAt; ++k) {
					mutable.set(i, j, k);
					if (ConfiguredBlockCondition.check(configuration.blockCondition(), entity.level(), mutable))
						count++;
				}
			}
		}
		return configuration.comparison().check(count);
	}
}
