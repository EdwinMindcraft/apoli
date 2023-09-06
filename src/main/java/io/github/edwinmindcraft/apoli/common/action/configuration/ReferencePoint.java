package io.github.edwinmindcraft.apoli.common.action.configuration;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

import static io.github.apace100.apoli.power.factory.condition.DistanceFromCoordinatesConditionRegistry.warnOnce;

public enum ReferencePoint {
	PLAYER_SPAWN,
	PLAYER_NATURAL_SPAWN,
	WORLD_SPAWN,
	WORLD_ORIGIN;

	@Nullable
	@Contract("_, _, false -> !null")
	public Vec3 getPoint(@Nullable Entity entity, @NotNull Level level, boolean allowNull) {
		switch (this) {
			case PLAYER_SPAWN:
			case PLAYER_NATURAL_SPAWN: // spawn not set through commands or beds/anchors
				if (entity instanceof Player) { // && data.getBoolean("check_modified_spawn")){
					warnOnce("Used reference '" + this.name().toLowerCase(Locale.ROOT) + "' which is not implemented yet, defaulting to world spawn.");
				}
				// No break on purpose (defaulting to world spawn)
				if (entity == null)
					warnOnce("Used entity-condition-only reference point in block condition, defaulting to world spawn.");
			case WORLD_SPAWN:
				if (allowNull && level.dimension() != Level.OVERWORLD)
					return null;
				LevelData data = level.getLevelData();
				BlockPos spawnPos = new BlockPos(data.getXSpawn(), data.getYSpawn(), data.getZSpawn());
				if (!level.getWorldBorder().isWithinBounds(spawnPos))
					spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int) level.getWorldBorder().getCenterX(), 0, (int) level.getWorldBorder().getCenterZ()));
				return new Vec3(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
			case WORLD_ORIGIN:
			default:
				return Vec3.ZERO;
		}
	}
}
