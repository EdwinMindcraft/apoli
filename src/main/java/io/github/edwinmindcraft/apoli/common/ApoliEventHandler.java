package io.github.edwinmindcraft.apoli.common;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.command.PowerCommand;
import io.github.apace100.apoli.command.ResourceCommand;
import io.github.apace100.apoli.util.InventoryUtil;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.component.PowerContainerImpl;
import io.github.edwinmindcraft.apoli.common.network.S2CCachedSpawnsPacket;
import io.github.edwinmindcraft.apoli.common.network.S2CSynchronizePowerContainer;
import io.github.edwinmindcraft.apoli.common.registry.ApoliCapabilities;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import io.github.edwinmindcraft.apoli.common.util.ModifyPlayerSpawnCache;
import io.github.edwinmindcraft.apoli.common.util.SpawnLookupUtil;
import io.github.edwinmindcraft.calio.api.event.CalioDynamicRegistryEvent;
import io.github.edwinmindcraft.calio.api.event.DynamicRegistrationEvent;
import net.minecraft.core.WritableRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class contains events that relate to non-power stuff, as in
 * synchronization & capability associations.
 */
@EventBusSubscriber(modid = Apoli.MODID)
public class ApoliEventHandler {

	@SubscribeEvent
	public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof LivingEntity living) {
			event.addCapability(PowerContainer.KEY, new PowerContainerImpl(living));
		}
	}

	@SubscribeEvent
	public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer sp) {
			PacketDistributor.sendToPlayer(sp, new S2CCachedSpawnsPacket(SpawnLookupUtil.getPowersWithSpawns()));
			S2CSynchronizePowerContainer packet = S2CSynchronizePowerContainer.forEntity(sp);
			if (packet == null) {
				Apoli.LOGGER.error("Couldn't create synchronization packet for player {}", sp.getScoreboardName());
				return;
			}
			PacketDistributor.sendToAllPlayers(packet);
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

    private static final Set<ResourceLocation> DISABLED_POWERS = new HashSet<>();

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPowerLoad(DynamicRegistrationEvent<ConfiguredPower<?, ?>> event) {
        if (event.getOriginal().getData().neoForgeConditions().isPresent() && !event.getOriginal().getData().neoForgeConditions().get().stream().allMatch(condition -> condition.test(ICondition.IContext.TAGS_INVALID)) {
            disablePower(event.getRegistryName());
            event.setCanceled(true);
        }
    }

    private static void disablePower(ResourceLocation powerId) {
        DISABLED_POWERS.add(powerId);
    }

    public static boolean isPowerDisabled(ResourceLocation powerId) {
        return DISABLED_POWERS.contains(powerId);
    }

	@SubscribeEvent
	public static void onDataSync(OnDatapackSyncEvent event) {
		if (event.getPlayer() == null) {
			for (ServerPlayer player : event.getPlayerList().getPlayers()) {
				PowerContainer container = PowerContainer.get(player);
				if (container != null) {
					container.rebuildCache();
					PowerContainer.sync(player);
				}
			}
		}
	}

	@SubscribeEvent
	public static void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		PowerContainer.get(event.getEntity()).ifPresent(x -> x.getPowers().forEach(y -> y.value().onRemoved(event.getEntity())));
	}

	@SubscribeEvent
	public static void livingTick(EntityTickEvent event) {
		if (!event.getEntity().level().isClientSide()) {
			PowerContainer container = PowerContainer.get(event.getEntity());
			if (container != null)
				container.serverTick();
		}

		// FIXME: Use an duck interface instead of a capability.
        InventoryUtil.forEachStack(event.getEntity(), (slot) -> {
            if (slot.get().isEmpty() || slot.get().getCapability(ApoliCapabilities.ENTITY_LINKED_ITEM_STACK).map(eli -> eli.getEntity() == event.getEntity()).orElse(false)) {
                return;
            }
            slot.get().getCapability(ApoliCapabilities.ENTITY_LINKED_ITEM_STACK).ifPresent(eli -> {
                eli.setEntity(event.getEntity());
            });
        });
	}

	// FIXME: Spawn powers.
	@SubscribeEvent
	public static void playerClone(PlayerEvent.Clone event) {
		event.getOriginal().reviveCaps(); // Revive capabilities.

		PowerContainer original = PowerContainer.get(event.getOriginal());
		PowerContainer player = PowerContainer.get(event.getEntity());
		if ((original == null) == (player != null)) {
			Apoli.LOGGER.info("Capability mismatch: original:{}, new:{}", original.isPresent(), player.isPresent());
		}
		original.ifPresent(x -> x.getPowers().forEach(y -> y.value().onRemoved(event.getOriginal())));
        player.ifPresent(p -> original.ifPresent(o -> p.readFromNbt(o.writeToNbt(new CompoundTag()))));
		if (!event.getEntity().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))
			PowerContainer.getPowers(event.getEntity(), ApoliPowers.KEEP_INVENTORY.get()).forEach(power -> power.value().getFactory().restoreItems(power.value(), event.getEntity()));

		if (event.getEntity() instanceof ServerPlayer)
			((ModifyPlayerSpawnCache)event.getEntity()).setActiveSpawnPower(((ModifyPlayerSpawnCache)event.getOriginal()).getActiveSpawnPower());

		event.getOriginal().invalidateCaps(); // Unload capabilities.
	}

	@SubscribeEvent
	public static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer sp) {
			PowerContainer.sync(sp);
			if (!event.isEndConquered()) {
				ApoliPowers.MODIFY_PLAYER_SPAWN.get().schedulePlayerToSpawn(sp);
				PowerContainer container = PowerContainer.get(sp);
				if (container != null)
					container.getPowers().forEach(y -> y.value().onRespawn(sp));
			}
		}
	}

	@SubscribeEvent
	public static void playerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity() instanceof ServerPlayer)
			PowerContainer.sync(event.getEntity());
	}

	@SubscribeEvent
	public static void trackNew(EntityJoinLevelEvent event) {
		if (event.getLevel().isClientSide())
			return;
		if (event.getEntity() instanceof LivingEntity le)
			PowerContainer.sync(le);
	}

	@SubscribeEvent
	public static void trackEntity(PlayerEvent.StartTracking event) {
		if (event.getEntity() instanceof ServerPlayer se && event.getTarget() instanceof LivingEntity target)
			PowerContainer.sync(target, se);
	}

	@SubscribeEvent
	public static void initializeCommands(RegisterCommandsEvent event) {
		PowerCommand.register(event.getDispatcher());
		ResourceCommand.register(event.getDispatcher());
	}
}
