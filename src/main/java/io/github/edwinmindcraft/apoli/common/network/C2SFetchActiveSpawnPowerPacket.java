package io.github.edwinmindcraft.apoli.common.network;

import io.github.apace100.apoli.util.ApoliConfigs;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.ModifyPlayerSpawnPower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyPlayerSpawnConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import io.github.edwinmindcraft.apoli.common.util.ModifyPlayerSpawnCache;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public class C2SFetchActiveSpawnPowerPacket implements CustomPacketPayload {
    public static final ResourceLocation ID = ApoliAPI.identifier("fetch_active_spawn_power");
    public static final CustomPacketPayload.Type<C2SFetchActiveSpawnPowerPacket> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SFetchActiveSpawnPowerPacket> STREAM_CODEC = StreamCodec.of(C2SFetchActiveSpawnPowerPacket::encode, C2SFetchActiveSpawnPowerPacket::decode);

    public static C2SFetchActiveSpawnPowerPacket decode(FriendlyByteBuf buffer) {
		return new C2SFetchActiveSpawnPowerPacket();
	}

	public static void encode(FriendlyByteBuf buffer, C2SFetchActiveSpawnPowerPacket packet) {

    }

    @SuppressWarnings("ConstantConditions")
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!ApoliConfigs.SERVER.separateSpawnFindingThread.get()) return;

            if (((ModifyPlayerSpawnCache)context.player()).getActiveSpawnPower() == null) {
                if (ApoliAPI.getPowerContainer(context.player()).hasPower(ApoliPowers.MODIFY_PLAYER_SPAWN.get())) {
                    Optional<Holder<ConfiguredPower<ModifyPlayerSpawnConfiguration, ModifyPlayerSpawnPower>>> holder = ApoliAPI.getPowerContainer(context.player()).getPowers(ApoliPowers.MODIFY_PLAYER_SPAWN.get()).stream().findFirst();
                    holder.ifPresent(configuredPowerHolder -> ApoliPowers.MODIFY_PLAYER_SPAWN.get().getSpawn(configuredPowerHolder.value(), context.player(), true));
                }
                return;
            }
            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new S2CActiveSpawnPowerPacket(Optional.of(((ModifyPlayerSpawnCache)context.player()).getActiveSpawnPower())));
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
