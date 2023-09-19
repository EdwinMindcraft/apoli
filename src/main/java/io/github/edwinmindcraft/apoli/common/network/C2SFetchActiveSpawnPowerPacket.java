package io.github.edwinmindcraft.apoli.common.network;

import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.ApoliCommon;
import io.github.edwinmindcraft.apoli.common.power.ModifyPlayerSpawnPower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyPlayerSpawnConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import io.github.edwinmindcraft.apoli.common.util.ModifyPlayerSpawnCache;
import net.minecraft.core.Holder;
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

    @SuppressWarnings("ConstantConditions")
    private void handleSync(ServerPlayer sender) {
        if (((ModifyPlayerSpawnCache)sender).getActiveSpawnPower() == null) {
            sender.reviveCaps();
            if (ApoliAPI.getPowerContainer(sender).hasPower(ApoliPowers.MODIFY_PLAYER_SPAWN.get())) {
                Optional<Holder<ConfiguredPower<ModifyPlayerSpawnConfiguration, ModifyPlayerSpawnPower>>> holder = ApoliAPI.getPowerContainer(sender).getPowers(ApoliPowers.MODIFY_PLAYER_SPAWN.get()).stream().findFirst();
                holder.ifPresent(configuredPowerHolder -> ApoliPowers.MODIFY_PLAYER_SPAWN.get().getSpawn(configuredPowerHolder.value(), sender, true));
            }
            sender.invalidateCaps();
            return;
        }
        ApoliCommon.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sender), new S2CActiveSpawnPowerPacket(Optional.of(((ModifyPlayerSpawnCache)sender).getActiveSpawnPower())));
    }

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> handleSync(contextSupplier.get().getSender()));
		contextSupplier.get().setPacketHandled(true);
	}
}
