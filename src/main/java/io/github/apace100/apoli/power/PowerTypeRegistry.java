package io.github.apace100.apoli.power;

import com.mojang.serialization.Lifecycle;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.calio.api.CalioAPI;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PowerTypeRegistry {

	/**
	 * @deprecated Not necessarily functional, although you shouldn't ever need to use this.
	 */
	@Deprecated
	public static <T extends Power> PowerType<T> register(ResourceLocation id, PowerType<T> powerType) {
		CalioAPI.getDynamicRegistries().get(ApoliDynamicRegistries.CONFIGURED_POWER_KEY)
				.register(ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, id), powerType.getConfiguredPower(), Lifecycle.experimental());
		return powerType;
	}

	/**
	 * @deprecated Not necessarily functional, although you shouldn't ever need to use this.
	 */
	@Deprecated
	protected static <T extends Power> PowerType<T> update(ResourceLocation id, PowerType<T> powerType) {
		CalioAPI.getDynamicRegistries().get(ApoliDynamicRegistries.CONFIGURED_POWER_KEY)
				.register(ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, id), powerType.getConfiguredPower(), Lifecycle.experimental());
		return powerType;
	}

	public static int size() {
		return ApoliAPI.getPowers().keySet().size();
	}

	public static Stream<ResourceLocation> identifiers() {
		return ApoliAPI.getPowers().keySet().stream();
	}

	public static Iterable<Map.Entry<ResourceLocation, PowerType<?>>> entries() {
		return ApoliAPI.getPowers().entrySet().stream().map(x -> Map.<ResourceLocation, PowerType<?>>entry(x.getKey().location(), x.getValue().getPowerType())).collect(Collectors.toSet());
	}

	public static Iterable<PowerType<?>> values() {
		return ApoliAPI.getPowers().stream().map(ConfiguredPower::getPowerType).collect(Collectors.toSet());
	}

	public static PowerType<?> get(ResourceLocation id) {
		return ApoliAPI.getPowers().getOptional(id).map(ConfiguredPower::getPowerType).orElseThrow(() -> new IllegalArgumentException("Could not get power type from id '" + id + "', as it was not registered!"));
	}

	public static ResourceLocation getId(PowerType<?> powerType) {
		return powerType.getIdentifier();
	}

	public static boolean contains(ResourceLocation id) {
		return ApoliAPI.getPowers().containsKey(id);
	}

	public static void clear() {
		//PowerClearCallback.EVENT.invoker().onPowerClear();
		//idToPower.clear();
	}

	public static void reset() {
		clear();
	}
}
