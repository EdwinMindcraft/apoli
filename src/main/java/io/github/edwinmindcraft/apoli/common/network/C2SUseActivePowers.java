package io.github.edwinmindcraft.apoli.common.network;

import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class C2SUseActivePowers implements CustomPacketPayload {
	public static final ResourceLocation ID = ApoliAPI.identifier("use_active_powers");
	public static final Type<C2SUseActivePowers> TYPE = new Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, C2SUseActivePowers> STREAM_CODEC = StreamCodec.of(C2SUseActivePowers::encode, C2SUseActivePowers::decode);

	public static C2SUseActivePowers decode(FriendlyByteBuf buffer) {
		int size = buffer.readVarInt();
		Set<ResourceLocation> set = new HashSet<>();
		for (int i = 0; i < size; i++) {
			set.add(buffer.readResourceLocation());
		}
		return new C2SUseActivePowers(set);
	}

	private final Set<ResourceLocation> powers;

	public C2SUseActivePowers(Set<ResourceLocation> powers) {
		this.powers = powers;
	}

	public static void encode(FriendlyByteBuf buffer, C2SUseActivePowers packet) {
		buffer.writeVarInt(packet.powers.size());
		packet.powers.forEach(buffer::writeResourceLocation);
	}

	public void handle(IPayloadContext context) {
		context.enqueueWork(() -> Optional.ofNullable(PowerContainer.get(context.player())).ifPresent(container -> this.powers.stream()
				.filter(container::hasPower)
				.map(container::getPower)
				.filter(Objects::nonNull)
				.forEach(x -> x.value().activate(context.player()))));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
