package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.BlockCollisionConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliDefaultConditions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public class BlockCollisionCondition extends EntityCondition<BlockCollisionConfiguration> {

	public BlockCollisionCondition() {
		super(BlockCollisionConfiguration.CODEC);
	}

	@Override
	public boolean check(BlockCollisionConfiguration configuration, Entity entity) {
		AABB boundingBox = entity.getBoundingBox();
		boundingBox = boundingBox.move(configuration.offset().multiply(boundingBox.getXsize(), boundingBox.getYsize(), boundingBox.getZsize()));
		if (configuration.blockCondition() == ApoliDefaultConditions.BLOCK_DEFAULT.getHolder().orElseThrow())
            return entity.level().getBlockCollisions(entity, boundingBox).iterator().hasNext();
        else {
            BlockPos minBlockPos = new BlockPos((int) (boundingBox.minX + 0.001), (int) (boundingBox.minY + 0.001), (int) (boundingBox.minZ + 0.001));
            BlockPos maxBlockPos = new BlockPos((int) (boundingBox.maxX + 0.001), (int) (boundingBox.maxY + 0.001), (int) (boundingBox.maxZ + 0.001));
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

            for (int x = minBlockPos.getX(); x <= maxBlockPos.getX(); x++) {
                for (int y = minBlockPos.getY(); y <= maxBlockPos.getY(); y++) {
                    for (int z = minBlockPos.getZ(); z <= maxBlockPos.getZ(); z++) {
                        mutable.set(x, y, z);
                        if (ConfiguredBlockCondition.check(configuration.blockCondition(), entity.level(), mutable, () -> entity.level().getBlockState(mutable)))
                            return true;
                    }
                }
            }
        }
        return false;
    }
}
