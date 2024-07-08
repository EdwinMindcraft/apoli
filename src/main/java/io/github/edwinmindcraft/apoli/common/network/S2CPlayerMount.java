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
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CPlayerMount(int entity, int vehicle) implements CustomPacketPayload {
	public static final ResourceLocation ID = ApoliAPI.identifier("player_mount");
	public static final CustomPacketPayload.Type<S2CPlayerMount> TYPE = new CustomPacketPayload.Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CPlayerMount> STREAM_CODEC = StreamCodec.of(S2CPlayerMount::encode, S2CPlayerMount::decode);

	public static S2CPlayerMount decode(FriendlyByteBuf buffer) {
		return new S2CPlayerMount(buffer.readInt(), buffer.readInt());
	}

	public static void encode(FriendlyByteBuf buffer, S2CPlayerMount packet) {
		buffer.writeInt(packet.entity());
		buffer.writeInt(packet.vehicle());
	}

	public void handle(IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientLevel level = Minecraft.getInstance().level;
			if (level == null)
				return;
			Entity entity = level.getEntity(this.entity());
			Entity vehicle = level.getEntity(this.vehicle());
			if (entity == null) {
				Apoli.LOGGER.warn("Received passenger for unknown player");
			} else if (vehicle == null) {
				Apoli.LOGGER.warn("Received unknown passenger for player");
			} else {
				if (entity.startRiding(vehicle, true))
					Apoli.LOGGER.info("{} started riding {}", entity.getDisplayName().getString(), vehicle.getDisplayName().getString());
				else
					Apoli.LOGGER.warn("{} failed to start riding {}", entity.getDisplayName().getString(), vehicle.getDisplayName().getString());
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
