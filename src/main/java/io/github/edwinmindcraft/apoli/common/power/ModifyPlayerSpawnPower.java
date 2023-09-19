package io.github.edwinmindcraft.apoli.common.power;

import com.google.common.collect.Sets;
import io.github.apace100.apoli.util.ApoliConfigs;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.ApoliCommon;
import io.github.edwinmindcraft.apoli.common.network.S2CActiveSpawnPowerPacket;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyPlayerSpawnConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import io.github.edwinmindcraft.apoli.common.util.ModifyPlayerSpawnCache;
import io.github.edwinmindcraft.apoli.common.util.SpawnLookupScheduler;
import io.github.edwinmindcraft.apoli.common.util.SpawnLookupUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Tuple;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ModifyPlayerSpawnPower extends PowerFactory<ModifyPlayerSpawnConfiguration> {

    public ModifyPlayerSpawnPower() {
        super(ModifyPlayerSpawnConfiguration.CODEC);
    }

    public void teleportToModifiedSpawn(ConfiguredPower<?, ?> configuration, Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            Tuple<ResourceKey<ConfiguredPower<?, ?>>, Tuple<ServerLevel, Vec3>> cachedSpawn = getSpawn(configuration, entity, false);
            if (cachedSpawn != null) {
                Tuple<ServerLevel, Vec3> spawn = cachedSpawn.getB();
                BlockPos spawnLocation = new BlockPos(spawn.getB().x, spawn.getB().y, spawn.getB().z).mutable();
                spawn.getA().getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(spawnLocation), 11, Unit.INSTANCE);
                serverPlayer.teleportTo(spawn.getA(), spawn.getB().x, spawn.getB().y, spawn.getB().z, entity.getXRot(), entity.getYRot());
            }
        }
    }

    public void checkSpawn(ServerPlayer entity) {
        @Nullable ResourceKey<ConfiguredPower<?, ?>> activeKey = ((ModifyPlayerSpawnCache)entity).getActiveSpawnPower();
        if (activeKey != null && SpawnLookupUtil.hasSpawnCached(activeKey)) {
            Tuple<ServerLevel, Vec3> spawn = SpawnLookupUtil.getSpawnCache(activeKey);

            if (spawn == null) return;

            Vec3 tpPos = DismountHelper.findSafeDismountLocation(EntityType.PLAYER, spawn.getA(), new BlockPos(spawn.getB()), true);
            if (tpPos != spawn.getB()) {
                SpawnLookupScheduler.INSTANCE.invalidate(activeKey);
            }
        }
    }

    public void resetSpawn(Optional<ConfiguredPower<ModifyPlayerSpawnConfiguration, ModifyPlayerSpawnPower>> configuration, Entity entity, boolean sendToClient) {
        if (entity instanceof ServerPlayer serverPlayer) {
            if (configuration.isPresent()) {
                getSpawn(configuration.get(), entity, sendToClient);
            } else {
                ((ModifyPlayerSpawnCache)entity).removeActiveSpawnPower();
                if (sendToClient) {
                    ApoliCommon.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new S2CActiveSpawnPowerPacket(Optional.empty()));
                }
            }
        }
    }

    @Nullable
    public Tuple<ResourceKey<ConfiguredPower<?, ?>>, Tuple<ServerLevel, Vec3>> getSpawn(ConfiguredPower<?, ?> configuration, Entity entity, boolean sendToClient) {
        if (!ApoliConfigs.SERVER.separateSpawnFindingThread.get()) {
            Optional<ResourceKey<ConfiguredPower<?, ?>>> key = ApoliAPI.getPowers().getResourceKey(configuration);
            if (key.isPresent()) {
                ResourceKey<ConfiguredPower<?, ?>> power = key.get();
                try {
                    SpawnLookupScheduler.INSTANCE.requestSpawn(power).get();
                } catch (InterruptedException | ExecutionException e) {
                    //Neither of those should ever be thrown, but better safe than sorry.
                    throw new RuntimeException(e);
                }
                Tuple<ServerLevel, Vec3> cachedSpawn = SpawnLookupUtil.getSpawnCache(power);
                if (cachedSpawn != null)
                    return new Tuple<>(power, cachedSpawn);
            }
            return null;
        }

        if (entity instanceof ServerPlayer serverPlayer) {
            @Nullable ResourceKey<ConfiguredPower<?, ?>> otherKey = ((ModifyPlayerSpawnCache)entity).getActiveSpawnPower();
            if (otherKey != null) {
                Tuple<ServerLevel, Vec3> activeSpawn = SpawnLookupUtil.getSpawnCache(otherKey);
                if (activeSpawn != null && ApoliAPI.getPowerContainer(entity).hasPower(otherKey))
                    return new Tuple<>(otherKey, activeSpawn);
            }

            Optional<ResourceKey<ConfiguredPower<?, ?>>> key = ApoliAPI.getPowers().getResourceKey(configuration);
            if (key.isPresent()) {
                ((ModifyPlayerSpawnCache)serverPlayer).setActiveSpawnPower(key.get());
                SpawnLookupScheduler.INSTANCE.requestSpawn(key.get());
                if (sendToClient)
                    ApoliCommon.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new S2CActiveSpawnPowerPacket(key));

                if (SpawnLookupUtil.getSpawnCache(key.get()) != null)
                    return new Tuple<>(key.get(), SpawnLookupUtil.getSpawnCache(key.get()));
            }
        }
        return null;
    }


    @Override
    public void onAdded(ConfiguredPower<ModifyPlayerSpawnConfiguration, ?> configuration, Entity entity) {
        if (ApoliConfigs.SERVER.separateSpawnFindingThread.get() && entity instanceof ServerPlayer serverPlayer && serverPlayer.isDeadOrDying() && ((ModifyPlayerSpawnCache)entity).getActiveSpawnPower() == null) {
            getSpawn(configuration, entity, true);
        }
    }


    @Override
    public void onRemoved(ConfiguredPower<ModifyPlayerSpawnConfiguration, ?> configuration, Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            if (!serverPlayer.hasDisconnected() && serverPlayer.getRespawnPosition() != null && serverPlayer.isRespawnForced())
                serverPlayer.setRespawnPosition(Level.OVERWORLD, null, 0F, false, false);
        }
    }

    // This is not the same tick method because we'd prefer it to only run once.
    public void tick(Entity entity) {
        if (ApoliConfigs.SERVER.separateSpawnFindingThread.get() && entity instanceof ModifyPlayerSpawnCache cache && entity instanceof ServerPlayer spe && cache.getActiveSpawnPower() != null) {
            IPowerContainer.get(entity).ifPresent(container -> {
                if ((!container.hasPower(cache.getActiveSpawnPower()) || !container.getPower(cache.getActiveSpawnPower()).isBound() || ApoliAPI.getPowers().containsKey(cache.getActiveSpawnPower()) && !ApoliAPI.getPowers().get(cache.getActiveSpawnPower()).isConfigurationValid() || ApoliAPI.getPowers().containsKey(cache.getActiveSpawnPower()) && !ApoliAPI.getPowers().get(cache.getActiveSpawnPower()).isActive(entity))) {
                    Optional<ConfiguredPower<ModifyPlayerSpawnConfiguration, ModifyPlayerSpawnPower>> optional = container.getPowers(ApoliPowers.MODIFY_PLAYER_SPAWN.get()).stream().filter(Holder::isBound).map(Holder::value).findFirst();
                    resetSpawn(optional, entity, spe.isDeadOrDying());
                }
            });
        }
    }

    private static final Set<ServerPlayer> PLAYERS_TO_RESPAWN = Sets.newHashSet();

    public void schedulePlayerToSpawn(ServerPlayer player) {
        PLAYERS_TO_RESPAWN.add(player);
    }

    @Override
    public void onRespawn(ConfiguredPower<ModifyPlayerSpawnConfiguration, ?> configuration, Entity entity) {
        if (entity instanceof ServerPlayer player && PLAYERS_TO_RESPAWN.contains(player) && player.getRespawnPosition() == null) {
            this.teleportToModifiedSpawn(configuration, entity);
            PLAYERS_TO_RESPAWN.remove(player);
        }
    }
}