package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public record ModifyPlayerSpawnConfiguration(ResourceKey<Level> dimension, float distanceMultiplier,
											 @Nullable ResourceKey<Biome> biome, String strategy,
											 @Nullable ResourceKey<Structure> structure,
											 @Nullable SoundEvent sound) implements IDynamicFeatureConfiguration {
	public static final Codec<ModifyPlayerSpawnConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.DIMENSION.fieldOf("dimension").forGetter(ModifyPlayerSpawnConfiguration::dimension),
			CalioCodecHelper.optionalField(CalioCodecHelper.FLOAT, "dimension_distance_multiplier", 0F).forGetter(ModifyPlayerSpawnConfiguration::distanceMultiplier),
			CalioCodecHelper.resourceKey(Registries.BIOME).optionalFieldOf("biome").forGetter(x -> Optional.ofNullable(x.biome())),
			CalioCodecHelper.optionalField(Codec.STRING, "spawn_strategy", "default").forGetter(ModifyPlayerSpawnConfiguration::strategy),
			CalioCodecHelper.optionalField(SerializableDataType.registryKey(Registries.STRUCTURE), "structure").forGetter(x -> Optional.ofNullable(x.structure())),
			CalioCodecHelper.optionalField(SerializableDataTypes.SOUND_EVENT, "respawn_sound").forGetter(x -> Optional.ofNullable(x.sound()))
	).apply(instance, (t1, t2, t3, t4, t5, t6) -> new ModifyPlayerSpawnConfiguration(t1, t2, t3.orElse(null), t4, t5.orElse(null), t6.orElse(null))));


	@Nullable
	private static Pair<BlockPos, Holder<Structure>> getStructureLocation(Level world, @Nullable ResourceKey<Structure> structure, @Nullable TagKey<Structure> structureTag, ResourceKey<Level> dimension) {
		Registry<Structure> registry = world.registryAccess().registryOrThrow(Registries.STRUCTURE);
		HolderSet<Structure> entryList = null;
		String structureOrTagName = "";
		if (structure != null) {
			var entry = registry.getHolder(structure);
			if (entry.isPresent()) {
				entryList = HolderSet.direct(entry.get());
			}
			structureOrTagName = structure.location().toString();
		}
		if (entryList == null && structureTag != null) {
			var optionalList = registry.getTag(structureTag);
			if (optionalList.isPresent()) {
				entryList = optionalList.get();
			}
			structureOrTagName = "#" + structureTag.location();
		}
		if (entryList == null) {
			Apoli.LOGGER.warn("Could not find feature: \"{}\" in registries.", structure);
			return null;
		}
		BlockPos blockPos = new BlockPos(0, 70, 0);
		ServerLevel serverWorld = ServerLifecycleHooks.getCurrentServer().getLevel(dimension);
		if (serverWorld == null) {
			Apoli.LOGGER.warn("No such dimension: {}", dimension.location());
			return null;
		}
		Pair<BlockPos, Holder<Structure>> result = serverWorld.getChunkSource().getGenerator().findNearestMapStructure(serverWorld, entryList, blockPos, 100, false);
		if (result == null) {
			Apoli.LOGGER.warn("Could not find structure \"{}\" in dimension: {}", structureOrTagName, dimension.location());
			return null;
		} else {
			return new Pair<>(result.getFirst(), result.getSecond());
		}
	}

	@Nullable
	private static Vec3 getValidSpawn(BlockPos startPos, int range, ServerLevel world) {
		//Force load the chunk in which we are working.
		//This method will generate the chunk if it needs to.
		world.getChunk(startPos.getX() >> 4, startPos.getZ() >> 4, ChunkStatus.FULL, true);
		// (di, dj) is a vector - direction in which we move right now
		// (di, dj) is a vector - direction in which we move right now
		int dx = 1;
		int dz = 0;
		// length of current segment
		int segmentLength = 1;
		BlockPos.MutableBlockPos mutable = startPos.mutable();
		// center of our starting structure, or dimension
		int center = startPos.getY();
		// Our valid spawn location
		Vec3 tpPos;

		// current position (x, z) and how much of current segment we passed
		int x = startPos.getX();
		int z = startPos.getZ();
		//position to check up, or down
		int segmentPassed = 0;
		// increase y check
		int i = 0;
		// Decrease y check
		int d = 0;
		while (i < world.getLogicalHeight() || d > 0) {
			for (int coordinateCount = 0; coordinateCount < range; ++coordinateCount) {
				// make a step, add 'direction' vector (di, dj) to current position (i, j)
				x += dx;
				z += dz;
				++segmentPassed;
				mutable.set(x, center + i, z);
				tpPos = DismountHelper.findSafeDismountLocation(EntityType.PLAYER, world, mutable, true);
				if (tpPos != null) {
					return (tpPos);
				} else {
					mutable.setY(center + d);
					tpPos = DismountHelper.findSafeDismountLocation(EntityType.PLAYER, world, mutable, true);
					if (tpPos != null) {
						return (tpPos);
					}
				}

				if (segmentPassed == segmentLength) {
					// done with current segment
					segmentPassed = 0;

					// 'rotate' directions
					int buffer = dx;
					dx = -dz;
					dz = buffer;

					// increase segment length if necessary
					if (dz == 0) {
						++segmentLength;
					}
				}
			}
			i++;
			d--;
		}
		return null;
	}

	@Nullable
	public Tuple<ServerLevel, BlockPos> getSpawn(Entity entity, boolean isSpawnObstructed) {
		if (entity instanceof ServerPlayer) {
			ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(this.dimension);
			BlockPos regularSpawn = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD)).getSharedSpawnPos();
			BlockPos spawnToDimPos;
			if (world == null) {
				Apoli.LOGGER.warn("Could not find dimension \"{}\".", this.dimension.toString());
				return null;
			}
			int center = world.getLogicalHeight() / 2;
			BlockPos.MutableBlockPos mutable;
			Vec3 tpPos;
			int range = 64;

			switch (this.strategy()) {
				case "center":
					spawnToDimPos = new BlockPos(0, center, 0);
					break;

				case "default":
					if (this.distanceMultiplier() != 0) {
						spawnToDimPos = new BlockPos((int) (regularSpawn.getX() * this.distanceMultiplier()), regularSpawn.getY(), (int) (regularSpawn.getZ() * this.distanceMultiplier()));
					} else {
						spawnToDimPos = new BlockPos(regularSpawn.getX(), regularSpawn.getY(), regularSpawn.getZ());
					}
					break;

				default:
					Apoli.LOGGER.warn("This case does nothing. The game crashes if there is no spawn strategy set");
					if (this.distanceMultiplier() != 0) {
						spawnToDimPos = new BlockPos((int) (regularSpawn.getX() * this.distanceMultiplier()), regularSpawn.getY(), (int) (regularSpawn.getZ() * this.distanceMultiplier()));
					} else {
						spawnToDimPos = new BlockPos(regularSpawn.getX(), regularSpawn.getY(), regularSpawn.getZ());
					}
			}

			if (this.biome() != null) {
				Pair<BlockPos, Holder<Biome>> biomePos = world.findClosestBiome3d(x -> x.is(this.biome()), spawnToDimPos, 6400, 8, 8);
				if (biomePos != null) {
					spawnToDimPos = biomePos.getFirst();
				} else {
					Apoli.LOGGER.warn("Could not find biome \"{}\" in dimension \"{}\".", this.biome(), this.dimension.toString());
				}
			}

			if (this.structure == null) {
				tpPos = getValidSpawn(spawnToDimPos, range, world);
			} else {
				Pair<BlockPos, Holder<Structure>> structure = getStructureLocation(world, this.structure, null, this.dimension);
				ChunkPos structureChunkPos;

				if (structure == null) {
					return null;
				}
				BlockPos structurePos = structure.getFirst();
				structureChunkPos = new ChunkPos(structurePos.getX() >> 4, structurePos.getZ() >> 4);
				StructureStart structureStart = world.structureManager().getStartForStructure(SectionPos.of(structureChunkPos, 0), structure.getSecond().value(), world.getChunk(structurePos));
				if (structureStart != null) {
					BlockPos structureCenter = new BlockPos(structureStart.getBoundingBox().getCenter());
					tpPos = getValidSpawn(structureCenter, range, world);
				} else
					tpPos = null;
			}

			if (tpPos != null) {
				mutable = new BlockPos((int) tpPos.x, (int) tpPos.y, (int) tpPos.z).mutable();
				BlockPos spawnLocation = mutable;
				world.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(spawnLocation), 11, Unit.INSTANCE);
				return new Tuple<>(world, spawnLocation);
			}
			return null;
		}
		return null;
	}
}
