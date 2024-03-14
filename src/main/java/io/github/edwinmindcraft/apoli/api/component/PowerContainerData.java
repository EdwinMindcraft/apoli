package io.github.edwinmindcraft.apoli.api.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.power.PowerSource;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Internal storage class for power related data.
 */
@ApiStatus.Internal
public final class PowerContainerData {
	public static final Codec<PowerContainerData> CODEC = RecordCodecBuilder.create(instance -> instance.group())

	public final Map<ResourceLocation, Set<PowerSource>> powers;
	public final Map<ResourceLocation, Holder<ConfiguredPower<?, ?>>> cached;

	public PowerContainerData() {
		this.powers = new HashMap<>();
		this.cached = new HashMap<>();
	}

	public void updateCache(RegistryAccess access) {
		Registry<ConfiguredPower<?, ?>> registry = access.registryOrThrow(ApoliDynamicRegistries.CONFIGURED_POWER_KEY);
		cached.clear();
		for (ResourceLocation location : powers.keySet()) {
			Optional<Holder.Reference<ConfiguredPower<?, ?>>> optional = registry.getHolder(ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, location));
			optional.ifPresent(holder -> cached.put(location, holder));
		}
	}
}
