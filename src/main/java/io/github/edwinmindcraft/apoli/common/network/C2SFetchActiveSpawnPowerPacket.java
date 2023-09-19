package io.github.edwinmindcraft.apoli.common.network;

import io.github.edwinmindcraft.apoli.common.ApoliCommon;
import io.github.edwinmindcraft.apoli.common.util.ModifyPlayerSpawnCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;
import java.util.function.Supplier;

public class C2SFetchActiveSpawnPowerPacket {
	public static C2SFetchActiveSpawnPowerPacket decode(FriendlyByteBuf buffer) {
		return new C2SFetchActiveSpawnPowerPacket();
	}

	public void encode(FriendlyByteBuf buffer) { }

    private void handleSync(ServerPlayer sender) {
        ApoliCommon.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sender), new S2CActiveSpawnPowerPacket(Optional.ofNullable(((ModifyPlayerSpawnCache)sender).getActiveSpawnPower())));
    }

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> handleSync(contextSupplier.get().getSender()));
		contextSupplier.get().setPacketHandled(true);
	}
}
