package io.github.edwinmindcraft.apoli.common.util;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class SpawnLookupUtil {
    private static final Set<ResourceKey<ConfiguredPower<?, ?>>> POWERS_WITH_SPAWNS = Collections.synchronizedSet(new HashSet<>());
    private static final Map<ResourceKey<ConfiguredPower<?, ?>>, Optional<Tuple<ServerLevel, Vec3>>> SPAWN_CACHE = Collections.synchronizedMap(new HashMap<>());

    public static Set<ResourceKey<ConfiguredPower<?, ?>>> getPowersWithSpawns() {
        return Set.copyOf(POWERS_WITH_SPAWNS);
    }

    public static boolean hasSpawnCached(ResourceKey<ConfiguredPower<?, ?>> key) {
        return POWERS_WITH_SPAWNS.contains(key);
    }

    public static Tuple<ServerLevel, Vec3> getSpawnCache(ResourceKey<ConfiguredPower<?, ?>> key) {
        return SPAWN_CACHE.getOrDefault(key, Optional.empty()).orElse(null);
    }

    public static void addToPowersWithSpawns(ResourceKey<ConfiguredPower<?, ?>> key) {
        POWERS_WITH_SPAWNS.add(key);
    }

    public static void changeSpawnCacheValue(ResourceKey<ConfiguredPower<?, ?>> key, ServerLevel level, Vec3 vec3) {
        SPAWN_CACHE.put(key, Optional.of(new Tuple<>(level, vec3)));
    }

    public static void emptySpawnCacheValue(ResourceKey<ConfiguredPower<?, ?>> key) {
        SPAWN_CACHE.put(key, Optional.empty());
    }

    public static void clearSpawnCacheValue(ResourceKey<ConfiguredPower<?, ?>> key) {
        POWERS_WITH_SPAWNS.remove(key);
        SPAWN_CACHE.remove(key);
    }

    public static void resetSpawnCache() {
        POWERS_WITH_SPAWNS.clear();
        SPAWN_CACHE.clear();
    }
}
