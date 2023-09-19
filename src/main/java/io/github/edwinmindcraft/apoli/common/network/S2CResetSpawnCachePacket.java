package io.github.edwinmindcraft.apoli.common.network;

import io.github.edwinmindcraft.apoli.common.util.SpawnLookupUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record S2CResetSpawnCachePacket() {

    public static S2CResetSpawnCachePacket decode(FriendlyByteBuf buf) {
        return new S2CResetSpawnCachePacket();
    }

    public void encode(FriendlyByteBuf buf) {}

    @OnlyIn(Dist.CLIENT)
    private void handleSync() {
        SpawnLookupUtil.resetSpawnCache();
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handleSync));
        contextSupplier.get().setPacketHandled(true);
    }
}
