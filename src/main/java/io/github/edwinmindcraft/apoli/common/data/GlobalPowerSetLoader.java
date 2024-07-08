package io.github.edwinmindcraft.apoli.common.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.common.global.GlobalPowerSet;
import io.github.edwinmindcraft.calio.api.registry.DynamicEntryFactory;
import io.github.edwinmindcraft.calio.api.registry.DynamicEntryValidator;
import io.github.edwinmindcraft.calio.api.registry.CalioDynamicRegistryManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public enum GlobalPowerSetLoader implements DynamicEntryFactory<GlobalPowerSet>, DynamicEntryValidator<GlobalPowerSet> {
	INSTANCE;

	private static final Comparator<GlobalPowerSet> LOADING_ORDER_COMPARATOR = Comparator.comparingInt(GlobalPowerSet::order);

	@Override
	public GlobalPowerSet accept(@NotNull ResourceLocation resourceLocation, @NotNull List<JsonElement> list) {
		Optional<GlobalPowerSet> definition = list.stream().flatMap(x -> {
			DataResult<GlobalPowerSet> power = GlobalPowerSet.CODEC.decode(JsonOps.INSTANCE, x).map(Pair::getFirst);
			Optional<GlobalPowerSet> powerDefinition = power.resultOrPartial(error -> {});
			if (power.error().isPresent()) {
				if (powerDefinition.isEmpty()) {
					Apoli.LOGGER.error("Error loading global power set \"{}\": {}", resourceLocation, power.error().get().message());
					return Stream.empty();
				} else
					Apoli.LOGGER.warn("Global Power Set \"{}\" will only be partially loaded: {}", resourceLocation, power.error().get().message());
			}
			return powerDefinition.stream();
		}).max(LOADING_ORDER_COMPARATOR);
		if (definition.isEmpty())
			Apoli.LOGGER.error("Loading for all instances of global power set {} failed.", resourceLocation);
		return definition.orElse(null);
	}

	@Override
	public @NotNull Map<ResourceLocation, GlobalPowerSet> create(ResourceLocation location, @NotNull List<JsonElement> entries) {
		GlobalPowerSet accept = this.accept(location, entries);
		if (accept != null) {
			ImmutableMap.Builder<ResourceLocation, GlobalPowerSet> builder = ImmutableMap.builder();
			builder.put(location, accept);
			return builder.build();
		}
		return ImmutableMap.of();
	}


	@Override
	public @NotNull DataResult<GlobalPowerSet> validate(@NotNull ResourceLocation location, @NotNull GlobalPowerSet globalPowerSet, @NotNull CalioDynamicRegistryManager manager) {
		return DataResult.success(globalPowerSet);
	}
}
