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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
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
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public record ModifyPlayerSpawnConfiguration(ResourceKey<Level> dimension, float distanceMultiplier,
											 @Nullable ResourceKey<Biome> biome, SpawnStrategy strategy,
											 @Nullable ResourceKey<Structure> structure,
											 @Nullable SoundEvent sound) implements IDynamicFeatureConfiguration {
	public static final Codec<ModifyPlayerSpawnConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.DIMENSION.fieldOf("dimension").forGetter(ModifyPlayerSpawnConfiguration::dimension),
			CalioCodecHelper.optionalField(CalioCodecHelper.FLOAT, "dimension_distance_multiplier", 0F).forGetter(ModifyPlayerSpawnConfiguration::distanceMultiplier),
			CalioCodecHelper.resourceKey(Registries.BIOME).optionalFieldOf("biome").forGetter(x -> Optional.ofNullable(x.biome())),
			CalioCodecHelper.optionalField(SerializableDataType.enumValue(SpawnStrategy.class), "spawn_strategy", SpawnStrategy.DEFAULT).forGetter(ModifyPlayerSpawnConfiguration::strategy),
			CalioCodecHelper.optionalField(SerializableDataType.registryKey(Registries.STRUCTURE), "structure").forGetter(x -> Optional.ofNullable(x.structure())),
			CalioCodecHelper.optionalField(SerializableDataTypes.SOUND_EVENT, "respawn_sound").forGetter(x -> Optional.ofNullable(x.sound()))
	).apply(instance, (t1, t2, t3, t4, t5, t6) -> new ModifyPlayerSpawnConfiguration(t1, t2, t3.orElse(null), t4, t5.orElse(null), t6.orElse(null))));


    public Optional<BlockPos> getBiomePos(ResourceLocation powerId, ServerLevel targetDimension, BlockPos originPos) {

        if (biome() == null) return Optional.empty();

        Optional<Biome> targetBiome = targetDimension.registryAccess().registryOrThrow(Registries.BIOME).getOptional(biome());
        if (targetBiome.isEmpty()) {
            Apoli.LOGGER.warn("Power {} could not set spawnpoint at biome \"{}\" as it's not registered in dimension \"{}\".", powerId, biome().location(), dimension().location());
            return Optional.empty();
        }

        com.mojang.datafixers.util.Pair<BlockPos, Holder<Biome>> targetBiomePos = targetDimension.findClosestBiome3d(
                biome -> biome.value() == targetBiome.get(),
                originPos,
                6400,
                8,
                8
        );

        if (targetBiomePos != null) return Optional.of(targetBiomePos.getFirst());
        else {
            Apoli.LOGGER.warn("Power {} could not set spawnpoint at biome \"{}\" as it couldn't be found in dimension \"{}\".", powerId, biome().location(), dimension().location());
            return Optional.empty();
        }

    }

    private Optional<Pair<BlockPos, Structure>> getStructurePos(ResourceLocation powerId, Level world, @Nullable ResourceKey<Structure> structure, @Nullable TagKey<Structure> structureTag, ResourceKey<Level> dimension) {

        Registry<Structure> structureRegistry = world.registryAccess().registryOrThrow(Registries.STRUCTURE);
        HolderSet<Structure> structureRegistryEntryList = null;
        String structureTagOrName = "";

        if (structure != null) {

            var entry = structureRegistry.getHolder(structure);
            if (entry.isPresent()) structureRegistryEntryList = HolderSet.direct(entry.get());

            structureTagOrName = structure.location().toString();

        }

        if (structureRegistryEntryList == null) {

            var entryList = structureRegistry.getTag(structureTag);
            if (entryList.isPresent()) structureRegistryEntryList = entryList.get();

            structureTagOrName = "#" + structureTag.location().toString();

        }

        MinecraftServer server = world.getServer();
        if (server == null) return Optional.empty();

        ServerLevel serverWorld = server.getLevel(dimension);
        if (serverWorld == null) return Optional.empty();

        BlockPos center = new BlockPos(0, 70, 0);
        com.mojang.datafixers.util.Pair<BlockPos, Holder<Structure>> structurePos = serverWorld
                .getChunkSource()
                .getGenerator()
                .findNearestMapStructure(
                        serverWorld,
                        structureRegistryEntryList,
                        center,
                        100,
                        false
                );

        if (structurePos == null) {
            Apoli.LOGGER.warn("Power {} could not set spawnpoint at structure \"{}\" as it couldn't be found in dimension \"{}\".", powerId, structureTagOrName, dimension.location());
            return Optional.empty();
        }

        else return Optional.of(new Pair<>(structurePos.getFirst(), structurePos.getSecond().value()));

    }


    public Optional<Vec3> getSpawnPos(ResourceLocation powerId, ServerLevel targetDimension, BlockPos originPos, int range) {

        if (this.structure() == null) return getValidSpawn(originPos, range, targetDimension);

        Optional<Pair<BlockPos, Structure>> targetStructure = getStructurePos(powerId, targetDimension, this.structure(), null, this.dimension());
        if (targetStructure.isEmpty()) return Optional.empty();

        BlockPos targetStructurePos = targetStructure.get().getFirst();
        ChunkPos targetStructureChunkPos = new ChunkPos(targetStructurePos.getX() >> 4, targetStructurePos.getZ() >> 4);

        StructureStart targetStructureStart = targetDimension.structureManager().getStartForStructure(SectionPos.of(targetStructureChunkPos, 0), targetStructure.get().getSecond(), targetDimension.getChunk(targetStructurePos));
        if (targetStructureStart == null) return Optional.empty();

        BlockPos targetStructureCenter = new BlockPos(targetStructureStart.getBoundingBox().getCenter());
        return getValidSpawn(targetStructureCenter, range, targetDimension);

    }

	private static Optional<Vec3> getValidSpawn(BlockPos startPos, int range, ServerLevel world) {
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
					return Optional.of(tpPos);
				} else {
					mutable.setY(center + d);
					tpPos = DismountHelper.findSafeDismountLocation(EntityType.PLAYER, world, mutable, true);
					if (tpPos != null) {
						return Optional.of(tpPos);
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
		return Optional.empty();
	}

    @Nullable
    public Tuple<ServerLevel, BlockPos> getSpawn(ResourceLocation powerId, Entity entity, boolean isSpawnObstructed) {
        if (entity instanceof ServerPlayer) {

        }
        return null;
    }

    public enum SpawnStrategy {
        CENTER((blockPos, center, multiplier) -> new BlockPos(0, center, 0)),
        DEFAULT(
                (blockPos, center, multiplier) -> {

                    BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();

                    if (multiplier != 0) mut.set(blockPos.getX() * multiplier, blockPos.getY(), blockPos.getZ() * multiplier);
                    else mut.set(blockPos);

                    return mut;

                }
        );

        final TriFunction<BlockPos, Integer, Float, BlockPos> strategyApplier;
        SpawnStrategy(TriFunction<BlockPos, Integer, Float, BlockPos> strategyApplier) {
            this.strategyApplier = strategyApplier;
        }

        public BlockPos apply(BlockPos blockPos, int center, float multiplier) {
            return strategyApplier.apply(blockPos, center, multiplier);
        }

    }
}
