package io.github.edwinmindcraft.apoli.common.network;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class C2SUseActivePowers {
	public static C2SUseActivePowers decode(FriendlyByteBuf buffer) {
		int size = buffer.readVarInt();
		Set<ResourceLocation> set = new HashSet<>();
		for (int i = 0; i < size; i++) {
			set.add(buffer.readResourceLocation());
		}
		return new C2SUseActivePowers(set);
	}

	private final Set<ResourceLocation> powers;

	public C2SUseActivePowers(Set<ResourceLocation> powers) {this.powers = powers;}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeVarInt(this.powers.size());
		this.powers.forEach(buffer::writeResourceLocation);
	}

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> PowerContainer.get(contextSupplier.get().getSender()).ifPresent(container -> this.powers.stream()
				.filter(container::hasPower)
				.map(container::getPower)
				.filter(Objects::nonNull)
				.forEach(x -> x.value().activate(contextSupplier.get().getSender()))));
		contextSupplier.get().setPacketHandled(true);
	}
}
