package io.github.edwinmindcraft.apoli.common;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.command.PowerCommand;
import io.github.apace100.apoli.command.ResourceCommand;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.component.IPowerDataCache;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.component.PowerContainer;
import io.github.edwinmindcraft.apoli.common.component.PowerDataCache;
import io.github.edwinmindcraft.apoli.common.network.S2CCachedSpawnsPacket;
import io.github.edwinmindcraft.apoli.common.network.S2CSynchronizePowerContainer;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import io.github.edwinmindcraft.apoli.common.util.ModifyPlayerSpawnCache;
import io.github.edwinmindcraft.apoli.common.util.SpawnLookupScheduler;
import io.github.edwinmindcraft.apoli.common.util.SpawnLookupUtil;
import io.github.edwinmindcraft.calio.api.event.CalioDynamicRegistryEvent;
import net.minecraft.core.WritableRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

/**
 * This class contains events that relate to non-power stuff, as in
 * synchronization & capability associations.
 */
@Mod.EventBusSubscriber(modid = Apoli.MODID)
public class ApoliEventHandler {

	@SubscribeEvent
	public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof LivingEntity living) {
			event.addCapability(IPowerContainer.KEY, new PowerContainer(living));
			event.addCapability(IPowerDataCache.KEY, new PowerDataCache());
		}
	}

	@SubscribeEvent
	public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer spe) {
            ApoliCommon.CHANNEL.send(PacketDistributor.PLAYER.with(() -> spe), new S2CCachedSpawnsPacket(SpawnLookupUtil.getPowersWithSpawns()));
			S2CSynchronizePowerContainer packet = S2CSynchronizePowerContainer.forEntity(spe);
			if (packet == null)
				Apoli.LOGGER.error("Couldn't create synchronization packet for player {}", spe.getScoreboardName());
			ApoliCommon.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
		}
	}

	@SubscribeEvent
	public static void calioLoadComplete(CalioDynamicRegistryEvent.LoadComplete event) {
		WritableRegistry<ConfiguredPower<?, ?>> configuredPowers = event.getRegistryManager().get(ApoliDynamicRegistries.CONFIGURED_POWER_KEY);
		configuredPowers.holders().forEach(holder -> {
			if (!holder.isBound())
				Apoli.LOGGER.info("Missing power: {}", holder.key());
			else {
				List<String> warnings = holder.value().getWarnings(event.getRegistryManager());
				List<String> errors = holder.value().getErrors(event.getRegistryManager());
				if (errors.isEmpty() && warnings.isEmpty()) return;
				Apoli.LOGGER.info("Status report for power: {}", holder.key());
				warnings.forEach(Apoli.LOGGER::warn);
				errors.forEach(Apoli.LOGGER::error);
			}
		});
	}

	@SubscribeEvent
	public static void onDataSync(OnDatapackSyncEvent event) {
		if (event.getPlayer() == null) {
			for (ServerPlayer player : event.getPlayerList().getPlayers()) {
				IPowerContainer.get(player).ifPresent(IPowerContainer::rebuildCache);
				IPowerContainer.sync(player);
			}
		}
	}

	@SubscribeEvent
	public static void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		IPowerContainer.get(event.getEntity()).ifPresent(x -> x.getPowers().forEach(y -> y.value().onRemoved(event.getEntity())));
	}

	@SubscribeEvent
	public static void livingTick(LivingEvent.LivingTickEvent event) {
		if (!event.getEntity().level.isClientSide())
			IPowerContainer.get(event.getEntity()).ifPresent(IPowerContainer::serverTick);
	}

	@SubscribeEvent
	public static void playerClone(PlayerEvent.Clone event) {
		event.getOriginal().reviveCaps(); // Revive capabilities.

		LazyOptional<IPowerContainer> original = IPowerContainer.get(event.getOriginal());
		LazyOptional<IPowerContainer> player = IPowerContainer.get(event.getEntity());
		if (original.isPresent() != player.isPresent()) {
			Apoli.LOGGER.info("Capability mismatch: original:{}, new:{}", original.isPresent(), player.isPresent());
		}
		original.ifPresent(x -> x.getPowers().forEach(y -> y.value().onRemoved(event.getOriginal())));
        player.ifPresent(p -> original.ifPresent(o -> p.readFromNbt(o.writeToNbt(new CompoundTag()))));

        if (event.getEntity() instanceof ServerPlayer) {
            ((ModifyPlayerSpawnCache)event.getEntity()).setActiveSpawnPower(((ModifyPlayerSpawnCache)event.getOriginal()).getActiveSpawnPower());
        }

		if (!event.getEntity().level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))
			IPowerContainer.getPowers(event.getEntity(), ApoliPowers.KEEP_INVENTORY.get()).forEach(power -> power.value().getFactory().restoreItems(power.value(), event.getEntity()));

		event.getOriginal().invalidateCaps(); // Unload capabilities.
	}

	@SubscribeEvent
	public static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer sp) {
			IPowerContainer.sync(sp);
			if (!event.isEndConquered()) {
                ApoliPowers.MODIFY_PLAYER_SPAWN.get().schedulePlayerToSpawn(sp);
                IPowerContainer.get(sp).ifPresent(x -> x.getPowers().forEach(y -> y.value().onRespawn(sp)));
            }
		}
	}

	@SubscribeEvent
	public static void playerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity() instanceof ServerPlayer)
			IPowerContainer.sync(event.getEntity());
	}

	@SubscribeEvent
	public static void trackNew(EntityJoinLevelEvent event) {
		if (event.getLevel().isClientSide())
			return;
		if (event.getEntity() instanceof LivingEntity le)
			IPowerContainer.sync(le);
	}

	@SubscribeEvent
	public static void trackEntity(PlayerEvent.StartTracking event) {
		if (event.getEntity() instanceof ServerPlayer se && event.getTarget() instanceof LivingEntity target)
			IPowerContainer.sync(target, se);
	}

	@SubscribeEvent
	public static void initializeCommands(RegisterCommandsEvent event) {
		PowerCommand.register(event.getDispatcher());
		ResourceCommand.register(event.getDispatcher());
	}
}
