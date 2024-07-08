package io.github.edwinmindcraft.apoli.common.network;

import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.common.util.SpawnLookupUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CResetSpawnCachePacket() implements CustomPacketPayload {
    public static final ResourceLocation ID = ApoliAPI.identifier("reset_spawn_cache");
    public static final CustomPacketPayload.Type<S2CResetSpawnCachePacket> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CResetSpawnCachePacket> STREAM_CODEC = StreamCodec.of(S2CResetSpawnCachePacket::encode, S2CResetSpawnCachePacket::decode);


    public static S2CResetSpawnCachePacket decode(FriendlyByteBuf buf) {
        return new S2CResetSpawnCachePacket();
    }

    public static void encode(FriendlyByteBuf buf, S2CResetSpawnCachePacket packet) {}

    public void handle(IPayloadContext context) {
        context.enqueueWork(SpawnLookupUtil::resetSpawnCache);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
