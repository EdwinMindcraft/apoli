package io.github.edwinmindcraft.apoli.common;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.command.PowerCommand;
import io.github.apace100.apoli.command.ResourceCommand;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.component.IPowerDataCache;
import io.github.edwinmindcraft.apoli.api.power.configuration.*;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.component.PowerContainer;
import io.github.edwinmindcraft.apoli.common.component.PowerDataCache;
import io.github.edwinmindcraft.apoli.common.data.PowerLoader;
import io.github.edwinmindcraft.apoli.common.network.S2CSynchronizePowerContainer;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
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
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
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
		if (event.getPlayer() instanceof ServerPlayer spe) {
			S2CSynchronizePowerContainer packet = S2CSynchronizePowerContainer.forEntity(spe);
			if (packet == null)
				Apoli.LOGGER.error("Couldn't create synchronization packet for player {}", spe.getScoreboardName());
			ApoliCommon.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
		}
	}

	@SubscribeEvent
	public static void calioRegistries(CalioDynamicRegistryEvent.Initialize event) {
		event.getRegistryManager().addForge(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, ApoliBuiltinRegistries.CONFIGURED_POWERS, ConfiguredPower.CODEC);

		event.getRegistryManager().addForge(ApoliDynamicRegistries.CONFIGURED_BIENTITY_ACTION_KEY, ApoliBuiltinRegistries.CONFIGURED_BIENTITY_ACTIONS, ConfiguredBiEntityAction.CODEC);
		event.getRegistryManager().addForge(ApoliDynamicRegistries.CONFIGURED_BLOCK_ACTION_KEY, ApoliBuiltinRegistries.CONFIGURED_BLOCK_ACTIONS, ConfiguredBlockAction.CODEC);
		event.getRegistryManager().addForge(ApoliDynamicRegistries.CONFIGURED_ENTITY_ACTION_KEY, ApoliBuiltinRegistries.CONFIGURED_ENTITY_ACTIONS, ConfiguredEntityAction.CODEC);
		event.getRegistryManager().addForge(ApoliDynamicRegistries.CONFIGURED_ITEM_ACTION_KEY, ApoliBuiltinRegistries.CONFIGURED_ITEM_ACTIONS, ConfiguredItemAction.CODEC);

		event.getRegistryManager().addForge(ApoliDynamicRegistries.CONFIGURED_BIENTITY_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_BIENTITY_CONDITIONS, ConfiguredBiEntityCondition.CODEC);
		event.getRegistryManager().addForge(ApoliDynamicRegistries.CONFIGURED_BIOME_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_BIOME_CONDITIONS, ConfiguredBiomeCondition.CODEC);
		event.getRegistryManager().addForge(ApoliDynamicRegistries.CONFIGURED_BLOCK_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_BLOCK_CONDITIONS, ConfiguredBlockCondition.CODEC);
		event.getRegistryManager().addForge(ApoliDynamicRegistries.CONFIGURED_DAMAGE_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_DAMAGE_CONDITIONS, ConfiguredDamageCondition.CODEC);
		event.getRegistryManager().addForge(ApoliDynamicRegistries.CONFIGURED_ENTITY_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_ENTITY_CONDITIONS, ConfiguredEntityCondition.CODEC);
		event.getRegistryManager().addForge(ApoliDynamicRegistries.CONFIGURED_FLUID_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_FLUID_CONDITIONS, ConfiguredFluidCondition.CODEC);
		event.getRegistryManager().addForge(ApoliDynamicRegistries.CONFIGURED_ITEM_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_ITEM_CONDITIONS, ConfiguredItemCondition.CODEC);

		event.getRegistryManager().addReload(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, "powers", PowerLoader.INSTANCE);
		event.getRegistryManager().addValidation(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, PowerLoader.INSTANCE, ApoliBuiltinRegistries.CONFIGURED_POWER_CLASS);
	}

	@SubscribeEvent
	public static void calioLoadComplete(CalioDynamicRegistryEvent.LoadComplete event) {
		WritableRegistry<ConfiguredPower<?, ?>> configuredPowers = event.getRegistryManager().get(ApoliDynamicRegistries.CONFIGURED_POWER_KEY);
		for (ConfiguredPower<?, ?> configuredPower : configuredPowers) {
			List<String> warnings = configuredPower.getWarnings(event.getRegistryManager());
			List<String> errors = configuredPower.getErrors(event.getRegistryManager());
			if (errors.isEmpty() && warnings.isEmpty()) continue;
			Apoli.LOGGER.info("Status report for power: {}", configuredPower.getRegistryName());
			warnings.forEach(Apoli.LOGGER::warn);
			errors.forEach(Apoli.LOGGER::error);
		}
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
		IPowerContainer.get(event.getEntityLiving()).ifPresent(x -> x.getPowers().forEach(y -> y.onRemoved(event.getEntityLiving())));
	}

	@SubscribeEvent
	public static void livingTick(LivingEvent.LivingUpdateEvent event) {
		if (!event.getEntityLiving().level.isClientSide())
			IPowerContainer.get(event.getEntityLiving()).ifPresent(IPowerContainer::serverTick);
	}

	@SubscribeEvent
	public static void playerClone(PlayerEvent.Clone event) {
		event.getOriginal().reviveCaps(); // Revive capabilities.

		LazyOptional<IPowerContainer> original = IPowerContainer.get(event.getOriginal());
		LazyOptional<IPowerContainer> player = IPowerContainer.get(event.getPlayer());
		if (original.isPresent() != player.isPresent()) {
			Apoli.LOGGER.info("Capability mismatch: original:{}, new:{}", original.isPresent(), player.isPresent());
		}
		player.ifPresent(p -> original.ifPresent(o -> p.readFromNbt(o.writeToNbt(new CompoundTag()))));
		original.ifPresent(x -> x.getPowers().forEach(y -> y.onRemoved(event.getOriginal())));
		if (!event.getEntityLiving().level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))
			IPowerContainer.getPowers(event.getPlayer(), ApoliPowers.KEEP_INVENTORY.get()).forEach(power -> power.getFactory().restoreItems(power, event.getPlayer()));

		event.getOriginal().invalidateCaps(); // Unload capabilities.
	}

	@SubscribeEvent
	public static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getPlayer() instanceof ServerPlayer sp) {
			IPowerContainer.sync(sp);
			if (!event.isEndConquered())
				IPowerContainer.get(sp).ifPresent(x -> x.getPowers().forEach(y -> y.onRespawn(sp)));
		}
	}

	@SubscribeEvent
	public static void playerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getPlayer() instanceof ServerPlayer)
			IPowerContainer.sync(event.getPlayer());
	}

	@SubscribeEvent
	public static void trackNew(EntityJoinWorldEvent event) {
		if (event.getWorld().isClientSide())
			return;
		if (event.getEntity() instanceof LivingEntity le)
			IPowerContainer.sync(le);
	}

	@SubscribeEvent
	public static void trackEntity(PlayerEvent.StartTracking event) {
		if (event.getPlayer() instanceof ServerPlayer se && event.getTarget() instanceof LivingEntity target)
			IPowerContainer.sync(target, se);
	}

	@SubscribeEvent
	public static void initializeCommands(RegisterCommandsEvent event) {
		PowerCommand.register(event.getDispatcher());
		ResourceCommand.register(event.getDispatcher());
	}
}
