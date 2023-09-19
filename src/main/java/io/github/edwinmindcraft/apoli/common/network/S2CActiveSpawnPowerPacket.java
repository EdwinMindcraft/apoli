package io.github.edwinmindcraft.apoli.common.network;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.util.ModifyPlayerSpawnCache;
import io.github.edwinmindcraft.apoli.common.util.SpawnLookupScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

// Only ever send this packet to a player who actively needs their active spawn power on the client.
// You'll get funky behaviour otherwise.
public record S2CActiveSpawnPowerPacket(Optional<ResourceKey<ConfiguredPower<?, ?>>> power) {
    public static S2CActiveSpawnPowerPacket decode(FriendlyByteBuf buf) {
        Optional<ResourceKey<ConfiguredPower<?, ?>>> power = Optional.empty();
        if (buf.readBoolean()) {
            power = Optional.of(buf.readResourceKey(ApoliDynamicRegistries.CONFIGURED_POWER_KEY));
        }
        return new S2CActiveSpawnPowerPacket(power);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(power().isPresent());
        if (power().isPresent()) {
            buf.writeResourceKey(power().get());
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void handleSync() {
        ((ModifyPlayerSpawnCache)Minecraft.getInstance().player).setActiveSpawnPower(this.power().orElse(null));
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handleSync));
        contextSupplier.get().setPacketHandled(true);
    }
}
