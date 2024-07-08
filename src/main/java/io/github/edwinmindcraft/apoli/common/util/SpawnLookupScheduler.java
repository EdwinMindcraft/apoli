package io.github.edwinmindcraft.apoli.common.util;

import com.google.common.util.concurrent.Futures;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.util.ApoliConfigs;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.network.S2CCachedSpawnsPacket;
import io.github.edwinmindcraft.apoli.common.network.S2CResetSpawnCachePacket;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyPlayerSpawnConfiguration;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

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
            PacketDistributor.sendToAllPlayers(new S2CCachedSpawnsPacket(Set.of(power), true));

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
        if (ServerLifecycleHooks.getCurrentServer() == null) return;
        PacketDistributor.sendToAllPlayers(new S2CResetSpawnCachePacket());
    }

    public void doSpawnLookup(ResourceKey<ConfiguredPower<?, ?>> power) {
        handlePower(power);
        CompletionTracker tracker = this.getTracker(power);
        if (tracker != null)
            tracker.complete();
    }

    private void handlePower(ResourceKey<ConfiguredPower<?, ?>> power) {
        ConfiguredPower<?, ?> configuredPower = ApoliAPI.getPowers().get(power);
        if (configuredPower.getConfiguration() instanceof ModifyPlayerSpawnConfiguration configuration) {
            ServerLevel targetDimension = ServerLifecycleHooks.getCurrentServer().getLevel(configuration.dimension());

            if (targetDimension == null) {
                Apoli.LOGGER.warn("Power {} could not set spawnpoint at dimension \"{}\" as it's not registered! Falling back to default spawnpoint...", power.location(), configuration.dimension().location());
                handleFailure(power);
                return;
            }

            BlockPos regularSpawn = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD)).getSharedSpawnPos();
            int center = targetDimension.getLogicalHeight() / 2;
            int range = 64;

            AtomicReference<Vec3> modifiedSpawnPos = new AtomicReference<>();

            BlockPos.MutableBlockPos modifiedSpawnBlockPos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos dimensionSpawnPos = configuration.strategy().apply(regularSpawn, center, configuration.distanceMultiplier()).mutable();

            configuration.getBiomePos(power.location(), targetDimension, dimensionSpawnPos).ifPresent(dimensionSpawnPos::set);
            configuration.getSpawnPos(power.location(), targetDimension, dimensionSpawnPos, range).ifPresent(modifiedSpawnPos::set);


            if (modifiedSpawnPos.get() == null) {
                handleFailure(power);
                return;
            }

            Vec3 msp = modifiedSpawnPos.get();
            modifiedSpawnBlockPos.set(msp.x, msp.y, msp.z);

            SpawnLookupUtil.changeSpawnCacheValue(power, targetDimension, msp);
            SpawnLookupUtil.addToPowersWithSpawns(power);
            PacketDistributor.sendToAllPlayers(new S2CCachedSpawnsPacket(Set.of(power)));
        } else {
            handleFailure(power);
        }
    }

    private void handleFailure(ResourceKey<ConfiguredPower<?, ?>> power) {
        SpawnLookupUtil.emptySpawnCacheValue(power);
        SpawnLookupUtil.addToPowersWithSpawns(power);
        PacketDistributor.sendToAllPlayers(new S2CCachedSpawnsPacket(Set.of(power)));
    }
}