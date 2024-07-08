package io.github.edwinmindcraft.apoli.common.network;

import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.util.ModifyPlayerSpawnCache;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

// Only ever send this packet to a player who actively needs their active spawn power on the client.
// You'll get funky behaviour otherwise.
public record S2CActiveSpawnPowerPacket(Optional<ResourceKey<ConfiguredPower<?, ?>>> power) implements CustomPacketPayload {
    public static final ResourceLocation ID = ApoliAPI.identifier("active_spawn_power");
    public static final CustomPacketPayload.Type<S2CActiveSpawnPowerPacket> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CActiveSpawnPowerPacket> STREAM_CODEC = StreamCodec.of(S2CActiveSpawnPowerPacket::encode, S2CActiveSpawnPowerPacket::decode);

    public static S2CActiveSpawnPowerPacket decode(FriendlyByteBuf buf) {
        Optional<ResourceKey<ConfiguredPower<?, ?>>> power = Optional.empty();
        if (buf.readBoolean()) {
            power = Optional.of(buf.readResourceKey(ApoliDynamicRegistries.CONFIGURED_POWER_KEY));
        }
        return new S2CActiveSpawnPowerPacket(power);
    }

    public static void encode(FriendlyByteBuf buf, S2CActiveSpawnPowerPacket packet) {
        buf.writeBoolean(packet.power().isPresent());
        if (packet.power().isPresent()) {
            buf.writeResourceKey(packet.power().get());
        }
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() ->
                ((ModifyPlayerSpawnCache)Minecraft.getInstance().player).setActiveSpawnPower(this.power().orElse(null))
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
