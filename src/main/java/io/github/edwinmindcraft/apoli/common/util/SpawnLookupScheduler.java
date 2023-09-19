package io.github.edwinmindcraft.apoli.common.util;

import com.google.common.util.concurrent.Futures;
import com.mojang.datafixers.util.Pair;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.util.ApoliConfigs;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.ApoliCommon;
import io.github.edwinmindcraft.apoli.common.network.S2CCachedSpawnsPacket;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyPlayerSpawnConfiguration;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.*;

public class SpawnLookupScheduler {
    public static final SpawnLookupScheduler INSTANCE = new SpawnLookupScheduler();

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    private static class CompletionTracker implements Future<Void> {
        private boolean isComplete;

        public void complete() {
            synchronized (this) {
                this.isComplete = true;
                this.notifyAll();
            }
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            synchronized (this) {
                return this.isComplete;
            }
        }

        @Override
        public Void get() throws InterruptedException {
            while (true) {
                synchronized (this) {
                    if (this.isComplete)
                        return null;
                }
                //Always timeout to avoid weird logic
                this.wait(1000L);
            }
        }

        @Override
        public Void get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, TimeoutException {
            long end = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(timeout, unit);
            while (true) {
                synchronized (this) {
                    if (this.isComplete)
                        return null;
                }
                if (end >= System.currentTimeMillis())
                    throw new TimeoutException();
                //Always timeout to avoid weird logic
                this.wait(Math.min(1000L, end - System.currentTimeMillis()));
            }
        }

    }

    private final HashMap<ResourceKey<ConfiguredPower<?, ?>>, CompletionTracker> trackers = new HashMap<>();
    private final Object2IntMap<ResourceKey<ConfiguredPower<?, ?>>> powers = new Object2IntOpenHashMap<>();
    private final HashSet<ResourceKey<ConfiguredPower<?, ?>>> handled = new HashSet<>();
    private boolean isRunning = false;

    private SpawnLookupScheduler() {
    }

    /**
     * Registers a power for lookup or increases priority if this power is already being looked up.
     * This should be called when a ModifySpawnPower is chosen, and optionally on world start.
     *
     * @param power The name of the power to increase the priority of.
     */
    public Future<Void> requestSpawn(ResourceKey<ConfiguredPower<?, ?>> power) {
        if (!ApoliConfigs.SERVER.separateSpawnFindingThread.get()) {
            if (!SpawnLookupUtil.hasSpawnCached(power))
                this.doSpawnLookup(power);
            return Futures.immediateVoidFuture();
        }
        synchronized (this) {
            CompletionTracker result;
            if (this.handled.contains(power))
                return Futures.immediateVoidFuture();
            this.powers.compute(power, (key, oldValue) -> oldValue != null ? oldValue + 1 : 0);
            synchronized (this.trackers) {
                result = this.trackers.computeIfAbsent(power, i -> new CompletionTracker());
            }
            if (!this.isRunning) {
                this.isRunning = true;
                this.queueNext();
            }
            return result;
        }
    }

    private CompletionTracker getTracker(ResourceKey<ConfiguredPower<?, ?>> power) {
        synchronized (this.trackers) {
            return this.trackers.computeIfAbsent(power, i -> new CompletionTracker());
        }
    }

    private void queueNext() {
        synchronized (this) {
            // At each step, lookup the most requested power, and treat it. If there is no maximum, max should return a random entry in the map.
            Optional<ResourceKey<ConfiguredPower<?, ?>>> next = this.powers.object2IntEntrySet().stream().max(Comparator.comparingInt(Object2IntMap.Entry::getIntValue)).map(Map.Entry::getKey);
            if (next.isEmpty()) {
                // Keep this in the synchronized block to ensure the thread restarts if it had stopped.
                this.isRunning = false;
                return;
            }
            ResourceKey<ConfiguredPower<?, ?>> power = next.get();
            this.handled.add(power);
            this.powers.removeInt(power);
            //This is technically tail recursive, meaning that we should be fine until we hit between 250 and 1000 powers ish,
            // or if the compiler optimizes calls, which I'm skeptical about.
            CompletableFuture.runAsync(() -> this.doSpawnLookup(power), EXECUTOR).thenRun(this::queueNext);
        }
    }

    /**
     * Marks a position as being invalid, and needing to be looked up again.
     */
    public void invalidate(ResourceKey<ConfiguredPower<?, ?>> power) {
        synchronized (this) {
            this.handled.remove(power);
            // Clear cache position for power.
            SpawnLookupUtil.clearSpawnCacheValue(power);
            ApoliCommon.CHANNEL.send(PacketDistributor.ALL.noArg(), new S2CCachedSpawnsPacket(Set.of(power), true));

            // The following isn't necessary if we're using lazy loading.
            // Adds the newly invalidated position to the positions to recompute.
            this.powers.compute(power, (key, oldValue) -> oldValue != null ? oldValue : 0);
            if (!this.isRunning) {
                this.isRunning = true;
                this.queueNext();
            }
        }
    }

    /**
     * Removes all lookup data from the thread.
     * This should be called whenever powers are reloaded.
     */
    public void clear() throws InterruptedException {
        // Clears a first time to interrupt any queued actions.
        synchronized (this) {
            this.powers.clear();
            this.handled.clear();
        }
        while (true) {
            boolean flag;
            synchronized (this) {
                flag = this.isRunning;
            }
            if (flag) {
                Thread.sleep(50);
            } else
                break;
        }
        // Clear a second time to make sure the states have been correctly invalidated.
        synchronized (this) {
            this.powers.clear();
            this.handled.clear();
        }
        // Clear cache position for all powers.
        SpawnLookupUtil.resetSpawnCache();
    }

    public void doSpawnLookup(ResourceKey<ConfiguredPower<?, ?>> power) {
        ConfiguredPower<?, ?> configuredPower = ApoliAPI.getPowers().get(power);
        if (configuredPower.getConfiguration() instanceof ModifyPlayerSpawnConfiguration configuration) {
            ResourceKey<Level> dimension = configuration.dimension();
            ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(dimension);
            BlockPos regularSpawn = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD)).getSharedSpawnPos();
            BlockPos spawnToDimPos;
            if (world == null) {
                Apoli.LOGGER.warn("Could not find dimension \"{}\".", dimension);
                return;
            }
            int center = world.getLogicalHeight() / 2;
            Vec3 tpPos;
            int range = 64;
            String strategy = configuration.strategy();
            float distanceMultiplier = configuration.distanceMultiplier();
            switch (strategy) {
                case "center" -> spawnToDimPos = new BlockPos(0, center, 0);
                case "default" -> {
                    if (distanceMultiplier != 0) {
                        spawnToDimPos = new BlockPos(regularSpawn.getX() * distanceMultiplier, regularSpawn.getY(), regularSpawn.getZ() * distanceMultiplier);
                    } else {
                        spawnToDimPos = new BlockPos(regularSpawn.getX(), regularSpawn.getY(), regularSpawn.getZ());
                    }
                }
                default -> {
                    Apoli.LOGGER.warn("This case does nothing. The game crashes if there is no spawn strategy set");
                    if (distanceMultiplier != 0) {
                        spawnToDimPos = new BlockPos(regularSpawn.getX() * distanceMultiplier, regularSpawn.getY(), regularSpawn.getZ() * distanceMultiplier);
                    } else {
                        spawnToDimPos = new BlockPos(regularSpawn.getX(), regularSpawn.getY(), regularSpawn.getZ());
                    }
                }
            }
            @Nullable ResourceKey<Biome> biome = configuration.biome();
            if (biome != null) {
                Pair<BlockPos, Holder<Biome>> biomePos = world.findClosestBiome3d(x -> x.is(biome), spawnToDimPos, 6400, 8, 8);
                if (biomePos != null) {
                    spawnToDimPos = biomePos.getFirst();
                } else {
                    Apoli.LOGGER.warn("Could not find biome \"{}\" in dimension \"{}\".", biome, dimension.location());
                }
            }
            @Nullable ResourceKey<Structure> structureKey = configuration.structure();
            if (structureKey == null) {
                tpPos = ModifyPlayerSpawnConfiguration.getValidSpawn(spawnToDimPos, range, world);
            } else {
                Pair<BlockPos, Holder<Structure>> structure = ModifyPlayerSpawnConfiguration.getStructureLocation(world, structureKey, null, dimension);
                ChunkPos structureChunkPos;
                if (structure == null) {
                    Apoli.LOGGER.warn("Could not find structure \"{}\" in dimension \"{}\".", structureKey.location(), dimension.location());
                    tpPos = null;
                } else {
                    BlockPos structurePos = structure.getFirst();
                    structureChunkPos = new ChunkPos(structurePos.getX() >> 4, structurePos.getZ() >> 4);
                    StructureStart structureStart = world.structureManager().getStartForStructure(SectionPos.of(structureChunkPos, 0), structure.getSecond().value(), world.getChunk(structurePos));
                    if (structureStart != null) {
                        BlockPos structureCenter = new BlockPos(structureStart.getBoundingBox().getCenter());
                        tpPos = ModifyPlayerSpawnConfiguration.getValidSpawn(structureCenter, range, world);
                    } else {
                        Apoli.LOGGER.warn("Could not find structure start for \"{}\" in dimension \"{}\".", structure, dimension.toString());
                        tpPos = null;
                    }
                }
            }

            if (tpPos != null) {
                SpawnLookupUtil.changeSpawnCacheValue(power, world, tpPos);
                SpawnLookupUtil.addToPowersWithSpawns(power);
                ApoliCommon.CHANNEL.send(PacketDistributor.ALL.noArg(), new S2CCachedSpawnsPacket(Set.of(power)));
            } else
                handleFailure(power);
        }
        CompletionTracker tracker = this.getTracker(power);
        if (tracker != null)
            tracker.complete();
    }

    private void handleFailure(ResourceKey<ConfiguredPower<?, ?>> power) {
        SpawnLookupUtil.emptySpawnCacheValue(power);
        SpawnLookupUtil.addToPowersWithSpawns(power);
        ApoliCommon.CHANNEL.send(PacketDistributor.ALL.noArg(), new S2CCachedSpawnsPacket(Set.of(power)));
    }
}