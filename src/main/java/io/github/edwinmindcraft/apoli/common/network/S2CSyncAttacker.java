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
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.OptionalInt;

public record S2CSyncAttacker(int self, OptionalInt attacker) implements CustomPacketPayload {
	public static final ResourceLocation ID = ApoliAPI.identifier("sync_attacker");
	public static final CustomPacketPayload.Type<S2CSyncAttacker> TYPE = new CustomPacketPayload.Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncAttacker> STREAM_CODEC = StreamCodec.of(S2CSyncAttacker::encode, S2CSyncAttacker::decode);

	public static S2CSyncAttacker decode(FriendlyByteBuf buf) {
		int self = buf.readInt();
		OptionalInt attacker = buf.readBoolean() ? OptionalInt.of(buf.readInt()) : OptionalInt.empty();
		return new S2CSyncAttacker(self, attacker);
	}

	public static void encode(FriendlyByteBuf buf, S2CSyncAttacker packet) {
		buf.writeInt(packet.self());
		buf.writeBoolean(packet.attacker().isPresent());
		packet.attacker().ifPresent(buf::writeInt);
	}

	public void handle(IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientLevel level = Minecraft.getInstance().level;
			if (level == null)
				return;

			Entity target = level.getEntity(this.self());
			if (!(target instanceof LivingEntity living)) {
				Apoli.LOGGER.warn("Received unknown target");
				return;
			}
			if (this.attacker().isPresent()) {
				Entity attacker = level.getEntity(this.attacker().getAsInt());
				if (!(attacker instanceof LivingEntity)) {
					Apoli.LOGGER.warn("Received unknown attacker");
					return;
				}
				living.setLastHurtByMob((LivingEntity) attacker);
			} else {
				living.setLastHurtByMob(null);
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
