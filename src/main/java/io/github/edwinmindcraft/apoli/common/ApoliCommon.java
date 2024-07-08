package io.github.edwinmindcraft.apoli.common;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.power.configuration.*;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.apoli.common.data.GlobalPowerSetLoader;
import io.github.edwinmindcraft.apoli.common.data.PowerLoader;
import io.github.edwinmindcraft.apoli.common.global.GlobalPowerSet;
import io.github.edwinmindcraft.apoli.common.network.*;
import io.github.edwinmindcraft.apoli.common.registry.*;
import io.github.edwinmindcraft.apoli.common.registry.action.*;
import io.github.edwinmindcraft.apoli.common.registry.condition.*;
import io.github.edwinmindcraft.apoli.compat.ApoliCompat;
import io.github.edwinmindcraft.calio.api.CalioAPI;
import io.github.edwinmindcraft.calio.api.event.CalioDynamicRegistryEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class ApoliCommon {
	public static final String NETWORK_VERSION = "2.0";

	public static final ResourceLocation POWER_SOURCE = Apoli.identifier("power_source");

	private static void initializeNetwork(RegisterPayloadHandlersEvent event) {
		event.registrar(CalioAPI.MODID)
				.versioned(NETWORK_VERSION)
				.playToServer(C2SUseActivePowers.TYPE, C2SUseActivePowers.STREAM_CODEC, C2SUseActivePowers::handle)
				.playToClient(S2CSynchronizePowerContainer.TYPE, S2CSynchronizePowerContainer.STREAM_CODEC, S2CSynchronizePowerContainer::handle)
				.playToClient(S2CPlayerDismount.TYPE, S2CPlayerDismount.STREAM_CODEC, S2CPlayerDismount::handle)
				.playToClient(S2CPlayerMount.TYPE, S2CPlayerMount.STREAM_CODEC, S2CPlayerMount::handle)
				.playToClient(S2CSyncAttacker.TYPE, S2CSyncAttacker.STREAM_CODEC, S2CSyncAttacker::handle)
				.playToClient(S2CCachedSpawnsPacket.TYPE, S2CCachedSpawnsPacket.STREAM_CODEC, S2CCachedSpawnsPacket::handle)
				.playToClient(S2CActiveSpawnPowerPacket.TYPE, S2CActiveSpawnPowerPacket.STREAM_CODEC, S2CActiveSpawnPowerPacket::handle)
				.playToServer(C2SFetchActiveSpawnPowerPacket.TYPE, C2SFetchActiveSpawnPowerPacket.STREAM_CODEC, C2SFetchActiveSpawnPowerPacket::handle)
				.playToClient(S2CResetSpawnCachePacket.TYPE, S2CResetSpawnCachePacket.STREAM_CODEC, S2CResetSpawnCachePacket::handle);
	}

	public static void initialize(IEventBus bus) {
		//Initialises registries.
		ApoliRegistries.bootstrap(bus);
		ApoliRegisters.initialize(bus);
		ApoliDynamicRegisters.initialize(bus);

		//Vanilla stuff
		ApoliRecipeSerializers.bootstrap();
		ApoliArgumentTypes.bootstrap();

		//Powers
		ApoliPowers.bootstrap();
		ApoliLootFunctions.bootstrap();
		ApoliLootConditions.bootstrap();

		//Actions
		ApoliBlockActions.bootstrap();
		ApoliEntityActions.bootstrap();
		ApoliItemActions.bootstrap();
		ApoliBiEntityActions.bootstrap();

		//Conditions
		ApoliBiomeConditions.bootstrap();
		ApoliBlockConditions.bootstrap();
		ApoliDamageConditions.bootstrap();
		ApoliEntityConditions.bootstrap();
		ApoliFluidConditions.bootstrap();
		ApoliItemConditions.bootstrap();
		ApoliBiEntityConditions.bootstrap();

		//Modifier operations
		ApoliModifierOperations.bootstrap();
		ApoliAttachments.bootstrap();

		ApoliArgumentTypes.initialize();

		bus.addListener(ApoliCommon::commonSetup);
		bus.addListener(ApoliCommon::initializeNetwork);
		bus.addListener(ApoliCommon::initalizeDynamicRegistries);
	}

	public static void commonSetup(FMLCommonSetupEvent event) {
		ApoliCompat.apply();
	}

	public static void initalizeDynamicRegistries(CalioDynamicRegistryEvent.Initialize event) {
		event.getRegistryManager().add(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, ConfiguredPower.DIRECT_CODEC);
		event.getRegistryManager().add(ApoliDynamicRegistries.GLOBAL_POWER_SET, GlobalPowerSet.DIRECT_CODEC);

		event.getRegistryManager().addReload(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, "powers", PowerLoader.INSTANCE);
		event.getRegistryManager().addReload(ApoliDynamicRegistries.GLOBAL_POWER_SET, "global_powers", GlobalPowerSetLoader.INSTANCE);
		event.getRegistryManager().addValidation(ApoliDynamicRegistries.GLOBAL_POWER_SET, GlobalPowerSetLoader.INSTANCE, ApoliDynamicRegistries.CONFIGURED_POWER_KEY);
	}
}
