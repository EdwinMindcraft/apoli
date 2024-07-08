package io.github.edwinmindcraft.apoli.common.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.integration.PowerLoadEvent;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.calio.api.registry.DynamicEntryFactory;
import io.github.edwinmindcraft.calio.api.registry.DynamicEntryValidator;
import io.github.edwinmindcraft.calio.api.registry.CalioDynamicRegistryManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public enum PowerLoader implements DynamicEntryFactory<ConfiguredPower<?, ?>>, DynamicEntryValidator<ConfiguredPower<?, ?>> {
	INSTANCE;

	private static final Comparator<ConfiguredPower<?, ?>> LOADING_ORDER_COMPARATOR = Comparator.comparingInt((ConfiguredPower<?, ?> x) -> x.getData().loadingPriority());

	@Override
	public ConfiguredPower<?, ?> accept(@NotNull ResourceLocation resourceLocation, @NotNull List<JsonElement> list) {
		SerializableData.CURRENT_NAMESPACE = resourceLocation.getNamespace();
		SerializableData.CURRENT_PATH = resourceLocation.getPath();
		Optional<ConfiguredPower<?, ?>> definition = list.stream().flatMap(x -> {
			PowerLoadEvent.Pre pre = new PowerLoadEvent.Pre(resourceLocation, x);
			NeoForge.EVENT_BUS.post(pre);
			if (pre.isCanceled()) return Stream.empty();
			DataResult<ConfiguredPower<?, ?>> power = ConfiguredPower.DIRECT_CODEC.decode(JsonOps.INSTANCE, pre.getJson()).map(Pair::getFirst);
			Optional<ConfiguredPower<?, ?>> powerDefinition = power.resultOrPartial(error -> {});
			if (power.error().isPresent()) {
				if (powerDefinition.isEmpty()) {
					Apoli.LOGGER.error("Error loading power \"{}\": {}", resourceLocation, power.error().get().message());
					return Stream.empty();
				} else
					Apoli.LOGGER.warn("Power \"{}\" will only be partially loaded: {}", resourceLocation, power.error().get().message());
			}
			powerDefinition.ifPresent(cp -> NeoForge.EVENT_BUS.post(new PowerLoadEvent.Post(resourceLocation, x, cp)));
			return powerDefinition.stream();
		}).max(LOADING_ORDER_COMPARATOR);
		SerializableData.CURRENT_NAMESPACE = null;
		SerializableData.CURRENT_PATH = null;
		if (definition.isEmpty())
			Apoli.LOGGER.error("Loading for all instances of power {} failed.", resourceLocation);
		return definition.orElse(null);
	}

	@Override
	public @NotNull Map<ResourceLocation, ConfiguredPower<?, ?>> create(ResourceLocation location, @NotNull List<JsonElement> entries) {
		ConfiguredPower<?, ?> accept = this.accept(location, entries);
		if (accept != null) {
			ImmutableMap.Builder<ResourceLocation, ConfiguredPower<?, ?>> builder = ImmutableMap.builder();
			accept.getContainedPowers().forEach((s, configuredPower) -> {
				ResourceLocation path = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath() + s);
				builder.put(path, configuredPower.value().complete(path));
			});
			builder.put(location, accept.complete(location));
			return builder.build();
		}
		return ImmutableMap.of();
	}


	@Override
	public @NotNull DataResult<ConfiguredPower<?, ?>> validate(@NotNull ResourceLocation location, @NotNull ConfiguredPower<?, ?> configuredPower, @NotNull CalioDynamicRegistryManager manager) {
		if (!configuredPower.isConfigurationValid()) {
			configuredPower.getErrors(manager).forEach(x -> Apoli.LOGGER.error("Error in power {}: {}", location, x));
			return DataResult.error(() -> "Invalid Configuration.");
		}
		return DataResult.success(configuredPower);
	}
}
