package io.github.edwinmindcraft.apoli.common.network;

import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.util.SpawnLookupUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;

public record S2CCachedSpawnsPacket(Set<ResourceKey<ConfiguredPower<?, ?>>> powers, boolean shouldRemove) implements CustomPacketPayload {
    public static final ResourceLocation ID = ApoliAPI.identifier("cached_spawns");
    public static final CustomPacketPayload.Type<S2CCachedSpawnsPacket> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CCachedSpawnsPacket> STREAM_CODEC = StreamCodec.of(S2CCachedSpawnsPacket::encode, S2CCachedSpawnsPacket::decode);

    public S2CCachedSpawnsPacket(Set<ResourceKey<ConfiguredPower<?, ?>>> powers) {
        this(powers, false);
    }

    public static S2CCachedSpawnsPacket decode(FriendlyByteBuf buf) {
        Set<ResourceKey<ConfiguredPower<?, ?>>> powers = new HashSet<>();
        int powerSize = buf.readInt();
        for (int i = 0; i < powerSize; ++i) {
            powers.add(buf.readResourceKey(ApoliDynamicRegistries.CONFIGURED_POWER_KEY));
        }
        boolean shouldRemove = buf.readBoolean();
        return new S2CCachedSpawnsPacket(powers, shouldRemove);
    }

    public static void encode(FriendlyByteBuf buf, S2CCachedSpawnsPacket packet) {
        buf.writeInt(packet.powers().size());
        packet.powers().forEach(buf::writeResourceKey);
        buf.writeBoolean(packet.shouldRemove());
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (shouldRemove())
                powers().forEach(SpawnLookupUtil::clearSpawnCacheValue);
            else
                powers().forEach(SpawnLookupUtil::addToPowersWithSpawns);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
