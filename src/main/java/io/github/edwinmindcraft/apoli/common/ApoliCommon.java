package io.github.edwinmindcraft.apoli.common;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.configuration.*;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.data.PowerLoader;
import io.github.edwinmindcraft.apoli.common.network.*;
import io.github.edwinmindcraft.apoli.common.registry.*;
import io.github.edwinmindcraft.apoli.common.registry.action.*;
import io.github.edwinmindcraft.apoli.common.registry.condition.*;
import io.github.edwinmindcraft.apoli.compat.ApoliCompat;
import io.github.edwinmindcraft.calio.api.event.CalioDynamicRegistryEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ApoliCommon {
	public static final String NETWORK_VERSION = "1.1";

	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(Apoli.identifier("channel"))
			.networkProtocolVersion(() -> NETWORK_VERSION)
			.clientAcceptedVersions(NETWORK_VERSION::equals)
			.serverAcceptedVersions(NETWORK_VERSION::equals)
			.simpleChannel();

	public static final ResourceLocation POWER_SOURCE = Apoli.identifier("power_source");

	private static void initializeNetwork() {
		int messageId = 0;
		CHANNEL.messageBuilder(C2SUseActivePowers.class, messageId++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(C2SUseActivePowers::encode).decoder(C2SUseActivePowers::decode)
				.consumerNetworkThread(C2SUseActivePowers::handle).add();

		CHANNEL.messageBuilder(S2CSynchronizePowerContainer.class, messageId++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(S2CSynchronizePowerContainer::encode).decoder(S2CSynchronizePowerContainer::decode)
				.consumerNetworkThread(S2CSynchronizePowerContainer::handle).add();

		CHANNEL.messageBuilder(S2CPlayerDismount.class, messageId++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(S2CPlayerDismount::encode).decoder(S2CPlayerDismount::decode)
				.consumerNetworkThread(S2CPlayerDismount::handle).add();

		CHANNEL.messageBuilder(S2CPlayerMount.class, messageId++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(S2CPlayerMount::encode).decoder(S2CPlayerMount::decode)
				.consumerNetworkThread(S2CPlayerMount::handle).add();

		CHANNEL.messageBuilder(S2CSyncAttacker.class, messageId++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(S2CSyncAttacker::encode).decoder(S2CSyncAttacker::decode)
				.consumerNetworkThread(S2CSyncAttacker::handle).add();

        CHANNEL.messageBuilder(S2CCachedSpawnsPacket.class, messageId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CCachedSpawnsPacket::encode).decoder(S2CCachedSpawnsPacket::decode)
                .consumerNetworkThread(S2CCachedSpawnsPacket::handle).add();

        CHANNEL.messageBuilder(S2CActiveSpawnPowerPacket.class, messageId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CActiveSpawnPowerPacket::encode).decoder(S2CActiveSpawnPowerPacket::decode)
                .consumerNetworkThread(S2CActiveSpawnPowerPacket::handle).add();

        CHANNEL.messageBuilder(C2SFetchActiveSpawnPowerPacket.class, messageId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SFetchActiveSpawnPowerPacket::encode).decoder(C2SFetchActiveSpawnPowerPacket::decode)
                .consumerNetworkThread(C2SFetchActiveSpawnPowerPacket::handle).add();

		Apoli.LOGGER.debug("Registered {} network messages.", messageId);
	}

	public static void initialize() {
		//Initialises registries.
		ApoliRegisters.initialize();
		ApoliDynamicRegisters.initialize();

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

		//Dynamic registries
		ApoliDefaultActions.bootstrap();
		ApoliDefaultConditions.bootstrap();

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(ApoliCommon::commonSetup);
		bus.addListener(ApoliCommon::initalizeDynamicRegistries);
	}

	public static void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(ApoliArgumentTypes::initialize);
		ApoliCompat.apply();
		initializeNetwork();
	}

	@SubscribeEvent
	public static void initalizeDynamicRegistries(CalioDynamicRegistryEvent.Initialize event) {
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

		event.getRegistryManager().addForge(ApoliDynamicRegistries.CONFIGURED_MODIFIER_KEY, ApoliBuiltinRegistries.CONFIGURED_MODIFIERS, ConfiguredModifier.CODEC);

		event.getRegistryManager().addReload(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, "powers", PowerLoader.INSTANCE);
		event.getRegistryManager().addValidation(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, PowerLoader.INSTANCE, ApoliBuiltinRegistries.CONFIGURED_POWER_CLASS);
	}
}
