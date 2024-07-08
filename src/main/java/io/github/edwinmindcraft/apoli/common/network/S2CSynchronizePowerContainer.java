package io.github.edwinmindcraft.apoli.common.network;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class S2CSynchronizePowerContainer implements CustomPacketPayload {
	public static final ResourceLocation ID = ApoliAPI.identifier("sync_power_container");
	public static final Type<S2CSynchronizePowerContainer> TYPE = new Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CSynchronizePowerContainer> STREAM_CODEC = StreamCodec.of(S2CSynchronizePowerContainer::encode, S2CSynchronizePowerContainer::decode);

	@Nullable
	public static S2CSynchronizePowerContainer forEntity(Entity living) {
		PowerContainer container = PowerContainer.get(living);
		if (container == null)
			return null;
		Multimap<ResourceLocation, ResourceLocation> powerSources = HashMultimap.create();
		Map<ResourceLocation, CompoundTag> data = new HashMap<>();
		for (ResourceKey<ConfiguredPower<?, ?>> power : container.getPowerTypes(true)) {
			for (ResourceLocation source : container.getSources(power)) {
				powerSources.put(power.location(), source);
			}
			Holder<ConfiguredPower<IDynamicFeatureConfiguration, PowerFactory<IDynamicFeatureConfiguration>>> configuredPower = container.getPower(power);
			if (configuredPower == null || !configuredPower.isBound()) {
				Apoli.LOGGER.warn("Invalid power container capability for entity {}", living.getScoreboardName());
				continue;
			}
			CompoundTag tag = configuredPower.value().serialize(container);
			if (tag.isEmpty())
				continue;
			data.put(power.location(), tag);
		}
		return new S2CSynchronizePowerContainer(living.getId(), powerSources, data);
	}

	public static S2CSynchronizePowerContainer decode(FriendlyByteBuf buffer) {
		Multimap<ResourceLocation, ResourceLocation> powerSources = HashMultimap.create();
		Map<ResourceLocation, CompoundTag> data = new HashMap<>();
		int entity = buffer.readInt();
		int powerCount = buffer.readVarInt();
		for (int i = 0; i < powerCount; i++) {
			ResourceLocation power = buffer.readResourceLocation();
			int sourceCount = buffer.readVarInt();
			for (int j = 0; j < sourceCount; j++) {
				ResourceLocation source = buffer.readResourceLocation();
				powerSources.put(power, source);
			}
			if (buffer.readBoolean()) {
				data.put(power, buffer.readNbt());
			}
		}
		return new S2CSynchronizePowerContainer(entity, powerSources, data);
	}

	private final int entity;
	private final Multimap<ResourceLocation, ResourceLocation> powerSources;
	private final Map<ResourceLocation, CompoundTag> data;

	public S2CSynchronizePowerContainer(int entity, Multimap<ResourceLocation, ResourceLocation> powerSources, Map<ResourceLocation, CompoundTag> data) {
		this.entity = entity;
		this.powerSources = powerSources;
		this.data = data;
	}

	public static void encode(RegistryFriendlyByteBuf buffer, S2CSynchronizePowerContainer packet) {
		buffer.writeInt(packet.entity);
		buffer.writeVarInt(packet.powerSources.keySet().size());
		for (Map.Entry<ResourceLocation, Collection<ResourceLocation>> powerEntry : packet.powerSources.asMap().entrySet()) {
			ResourceLocation power = powerEntry.getKey();
			Collection<ResourceLocation> sources = powerEntry.getValue();
			buffer.writeResourceLocation(power);
			buffer.writeVarInt(sources.size());
			sources.forEach(buffer::writeResourceLocation);
			CompoundTag tag = packet.data.get(power);
			if (tag == null || tag.isEmpty())
				buffer.writeBoolean(false);
			else {
				buffer.writeBoolean(true);
				buffer.writeNbt(tag);
			}
		}
	}

	public void handle(IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientLevel level = Minecraft.getInstance().level;
			if (level == null)
				return;
			Entity entity = level.getEntity(this.entity);
			if (entity instanceof LivingEntity living && PowerContainer.get(living) != null)
				PowerContainer.get(living).handle(this.powerSources, this.data);
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
