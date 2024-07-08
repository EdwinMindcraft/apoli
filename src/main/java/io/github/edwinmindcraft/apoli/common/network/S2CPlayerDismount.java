package io.github.edwinmindcraft.apoli.common.network;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CPlayerDismount(int entity) implements CustomPacketPayload {
	public static final ResourceLocation ID = ApoliAPI.identifier("player_dismount");
	public static final Type<S2CPlayerDismount> TYPE = new CustomPacketPayload.Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CPlayerDismount> STREAM_CODEC = StreamCodec.of(S2CPlayerDismount::encode, S2CPlayerDismount::decode);

	public static S2CPlayerDismount decode(FriendlyByteBuf buffer) {
		return new S2CPlayerDismount(buffer.readInt());
	}

	public static void encode(FriendlyByteBuf buffer, S2CPlayerDismount packet) {
		buffer.writeInt(packet.entity());
	}

	public void handle(IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientLevel level = Minecraft.getInstance().level;
			if (level == null)
				return;
			Entity entity = level.getEntity(this.entity());
			if (entity == null) {
				Apoli.LOGGER.warn("Unknown player tried to dismount");
			} else {
				if (entity.getVehicle() instanceof Player)
					entity.removeVehicle();
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
