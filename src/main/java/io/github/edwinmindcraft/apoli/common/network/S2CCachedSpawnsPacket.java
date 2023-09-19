package io.github.edwinmindcraft.apoli.common.network;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.util.SpawnLookupScheduler;
import io.github.edwinmindcraft.apoli.common.util.SpawnLookupUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public record S2CCachedSpawnsPacket(Set<ResourceKey<ConfiguredPower<?, ?>>> powers, boolean shouldRemove) {
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

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.powers().size());
        this.powers().forEach(buf::writeResourceKey);
        buf.writeBoolean(this.shouldRemove());
    }

    @OnlyIn(Dist.CLIENT)
    private void handleSync() {
        if (shouldRemove())
            powers().forEach(SpawnLookupUtil::clearSpawnCacheValue);
        else
            powers().forEach(SpawnLookupUtil::addToPowersWithSpawns);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handleSync));
        contextSupplier.get().setPacketHandled(true);
    }
}
