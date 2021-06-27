package dev.experimental.apoli.api.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import dev.experimental.apoli.api.ApoliAPI;
import dev.experimental.apoli.api.power.factory.*;
import io.github.apace100.apoli.Apoli;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;

public class ApoliRegistries {

	public static final RegistryKey<net.minecraft.util.registry.Registry<PowerFactory<?>>> POWER_FACTORY_KEY = RegistryKey.ofRegistry(ApoliAPI.identifier("power_factory"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<EntityCondition<?>>> ENTITY_CONDITION_KEY = RegistryKey.ofRegistry(ApoliAPI.identifier("entity_condition"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<ItemCondition<?>>> ITEM_CONDITION_KEY = RegistryKey.ofRegistry(ApoliAPI.identifier("item_condition"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<BlockCondition<?>>> BLOCK_CONDITION_KEY = RegistryKey.ofRegistry(ApoliAPI.identifier("block_condition"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<DamageCondition<?>>> DAMAGE_CONDITION_KEY = RegistryKey.ofRegistry(ApoliAPI.identifier("damage_condition"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<FluidCondition<?>>> FLUID_CONDITION_KEY = RegistryKey.ofRegistry(ApoliAPI.identifier("fluid_condition"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<BiomeCondition<?>>> BIOME_CONDITION_KEY = RegistryKey.ofRegistry(ApoliAPI.identifier("biome_condition"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<EntityAction<?>>> ENTITY_ACTION_KEY = RegistryKey.ofRegistry(ApoliAPI.identifier("entity_action"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<ItemAction<?>>> ITEM_ACTION_KEY = RegistryKey.ofRegistry(ApoliAPI.identifier("item_action"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<BlockAction<?>>> BLOCK_ACTION_KEY = RegistryKey.ofRegistry(ApoliAPI.identifier("block_action"));

	public static final Registrar<PowerFactory<?>> POWER_FACTORY;
	public static final Registrar<EntityCondition<?>> ENTITY_CONDITION;
	public static final Registrar<ItemCondition<?>> ITEM_CONDITION;
	public static final Registrar<BlockCondition<?>> BLOCK_CONDITION;
	public static final Registrar<DamageCondition<?>> DAMAGE_CONDITION;
	public static final Registrar<FluidCondition<?>> FLUID_CONDITION;
	public static final Registrar<BiomeCondition<?>> BIOME_CONDITION;
	public static final Registrar<EntityAction<?>> ENTITY_ACTION;
	public static final Registrar<ItemAction<?>> ITEM_ACTION;
	public static final Registrar<BlockAction<?>> BLOCK_ACTION;

	static {
		Registries registries = Registries.get(Apoli.MODID);
		//TODO All network calls after login should use integer instead of powers.
		POWER_FACTORY = registries.<PowerFactory<?>>builder(POWER_FACTORY_KEY.getValue()).syncToClients().build();
		ENTITY_CONDITION = registries.<EntityCondition<?>>builder(ENTITY_CONDITION_KEY.getValue()).syncToClients().build();
		ITEM_CONDITION = registries.<ItemCondition<?>>builder(ITEM_CONDITION_KEY.getValue()).syncToClients().build();
		BLOCK_CONDITION = registries.<BlockCondition<?>>builder(BLOCK_CONDITION_KEY.getValue()).syncToClients().build();
		DAMAGE_CONDITION = registries.<DamageCondition<?>>builder(DAMAGE_CONDITION_KEY.getValue()).syncToClients().build();
		FLUID_CONDITION = registries.<FluidCondition<?>>builder(FLUID_CONDITION_KEY.getValue()).syncToClients().build();
		BIOME_CONDITION = registries.<BiomeCondition<?>>builder(BIOME_CONDITION_KEY.getValue()).syncToClients().build();
		ENTITY_ACTION = registries.<EntityAction<?>>builder(ENTITY_ACTION_KEY.getValue()).syncToClients().build();
		ITEM_ACTION = registries.<ItemAction<?>>builder(ITEM_ACTION_KEY.getValue()).syncToClients().build();
		BLOCK_ACTION = registries.<BlockAction<?>>builder(BLOCK_ACTION_KEY.getValue()).syncToClients().build();
	}

	/**
	 * This is basically {@link net.minecraft.util.registry.Registry}, just altered in such a way that it works with
	 * architectury's registries.
	 *
	 * @param registry The registry to create the codec for.
	 *
	 * @return The new codec.
	 */
	public static <T> Codec<T> codec(Registrar<T> registry) {
		return new Codec<>() {
			@Override
			public <U> DataResult<Pair<T, U>> decode(DynamicOps<U> dynamicOps, U input) {
				return dynamicOps.compressMaps() ? dynamicOps.getNumberValue(input).flatMap((number) -> {
					T object = registry.byRawId(number.intValue());
					return object == null ? DataResult.error("Unknown registry id: " + number) : DataResult.success(object);
				}).map((objectx) -> Pair.of(objectx, dynamicOps.empty())) : Identifier.CODEC.decode(dynamicOps, input).flatMap((pair) -> {
					T object = registry.get(pair.getFirst());
					return object == null ? DataResult.error("Unknown registry key: " + pair.getFirst()) : DataResult.success(Pair.of(object, pair.getSecond()));
				});
			}

			@Override
			public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
				Identifier identifier = registry.getId(input);
				if (identifier == null) {
					return DataResult.error("Unknown registry element " + input);
				} else {
					return ops.compressMaps() ?
							ops.mergeToPrimitive(prefix, ops.createInt(registry.getRawId(input))) :
							ops.mergeToPrimitive(prefix, ops.createString(identifier.toString()));
				}
			}
		};
	}
}
