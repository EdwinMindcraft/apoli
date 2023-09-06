package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.apace100.apoli.util.Shape;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.BlockInRadiusConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.function.BooleanSupplier;

public class BlockInRadiusCondition extends EntityCondition<BlockInRadiusConfiguration> {

	public BlockInRadiusCondition() {
		super(BlockInRadiusConfiguration.CODEC);
	}

	@Override
	public boolean check(BlockInRadiusConfiguration configuration, Entity entity) {
		int stopAt = configuration.comparison().getOptimalStoppingPoint();
		MutableInt count = new MutableInt(0);
		BooleanSupplier stopping = () -> count.intValue() >= stopAt;
		int radius = configuration.radius();
		BlockPos lowerCorner = entity.blockPosition().offset(-radius, -radius, -radius);
		BlockPos upperCorner = entity.blockPosition().offset(radius, radius, radius);
		int xStart = lowerCorner.getX() >> 4;
		int xEnd = upperCorner.getX() >> 4;
		int yStart = lowerCorner.getY() >> 4;
		int yEnd = upperCorner.getY() >> 4;
		int zStart = lowerCorner.getZ() >> 4;
		int zEnd = upperCorner.getZ() >> 4;
		int xSize = xEnd - xStart + 1;
		int ySize = yEnd - yStart + 1;
		int zSize = zEnd - zStart + 1;
		LevelChunkSection[] sections = new LevelChunkSection[xSize * ySize * zSize];
		for (int x = xStart; x <= xEnd; ++x) {
			for (int z = zStart; z <= zEnd; ++z) {
				ChunkAccess chunk = entity.level().getChunk(x, z); //Chunk Will be force loaded.
				LevelChunkSection[] chunkSections = chunk.getSections();
				for (int y = yStart; y <= yEnd; ++y) {
					int sectionIndex = chunk.getSectionIndex(y << 4);//16 y
					int index = (y - yStart) + (((x - xStart) + (xSize * (z - zStart))) * ySize);
					if (sectionIndex >= 0 & sectionIndex < chunkSections.length)
						sections[index] = chunkSections[sectionIndex];
					else
						sections[index] = null;
				}
			}
		}
		Shape.forPositions(entity.blockPosition(), configuration.shape(), radius, pos -> {
			int x = pos.getX() >> 4;
			int y = pos.getY() >> 4;
			int z = pos.getZ() >> 4;
			LevelChunkSection section = sections[y - yStart + (x - xStart + xSize * (z - zStart)) * ySize];
			if (ConfiguredBlockCondition.check(configuration.blockCondition(), entity.level(), pos, () -> section == null ? Blocks.VOID_AIR.defaultBlockState() : section.getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15)))
				count.increment();
		}, stopping);
		return configuration.comparison().check(count.intValue());
	}

	@Override
	public boolean check(BlockInRadiusConfiguration configuration, ConditionData data, Entity entity) {
		if (data.inverted())
			return this.check(configuration.inverse(), entity);
		return this.check(configuration, entity);
	}
}
