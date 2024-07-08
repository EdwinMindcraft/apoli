package io.github.edwinmindcraft.apoli.api.power.factory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.IFactory;
import io.github.edwinmindcraft.apoli.api.power.PowerData;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class PowerFactory<T extends IDynamicFeatureConfiguration> {
	public static final Codec<PowerFactory<?>> CODEC = ApoliRegistries.POWER_FACTORY.byNameCodec();
	private static final Map<String, ResourceLocation> ALIASES = Util.make(() -> {
		ImmutableMap.Builder<String, ResourceLocation> builder = ImmutableMap.builder();
		try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(PowerFactory.class.getResourceAsStream("/data/apoli/power_class_registry.json")))) {
			Gson gson = new GsonBuilder().create();
			JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
			for (String s : jsonObject.keySet()) {
				builder.put(s, ResourceLocation.parse(jsonObject.get(s).getAsString()));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return builder.build();
	});

	private static <T extends IDynamicFeatureConfiguration, F extends PowerFactory<T>> MapCodec<ConfiguredPower<T, ?>> powerCodec(MapCodec<T> codec, F factory) {
		return IFactory.unionCodec(codec, PowerData.CODEC, factory::configure, ConfiguredPower::getConfiguration, ConfiguredPower::getData);
	}

	public static final Codec<PowerFactory<?>> IGNORE_NAMESPACE_CODEC = Codec.STRING.flatXmap(val -> {
		if (val.startsWith("io.github.apace100.apoli.power."))
			val = val.substring("io.github.apace100.apoli.power.".length());
		ResourceLocation temp = ALIASES.get(val);
		ResourceLocation id = temp != null ? temp : ResourceLocation.tryParse(val);
		if (id == null) {
			String failedString = val;
			return DataResult.error(() -> "Failed to convert \"" + failedString + "\" to a resource location");
		}
		PowerFactory<?> value = ApoliRegistries.POWER_FACTORY.get(id);
		if (value != null)
			return DataResult.success(value); //Avoid the slow code if we can.
		return ApoliRegistries.POWER_FACTORY.entrySet().stream()
				.filter(entry -> entry.getKey().location().getPath().equals(id.getPath()))
				.findFirst().map(Map.Entry::getValue)
				.map(DataResult::success)
				.orElseGet(() -> DataResult.error(() -> "Failed to find power factory with path: " + id.getPath()));
	}, factory -> {
		var key = ApoliRegistries.POWER_FACTORY.getKey(factory);
		if (key != null)
			return DataResult.success(key.toString());
		else
			return DataResult.error(() -> "Unregistered power factory: %s".formatted(factory));
	});

	private final MapCodec<ConfiguredPower<T, ?>> codec;
	private final boolean allowConditions;
	private boolean ticking = false;
	private boolean tickingWhenInactive = false;

	protected PowerFactory(MapCodec<T> codec) {
		this(codec, true);
	}

	/**
	 * Creates a new power factory.
	 *
	 * @param codec           The codec used to serialize the configuration of this power.
	 * @param allowConditions Determines whether this power will use the global field {@link PowerData#conditions()} or not.
	 * @see #PowerFactory(MapCodec) for a version with allow conditions true by default.
	 */
	protected PowerFactory(MapCodec<T> codec, boolean allowConditions) {
		this.codec = powerCodec(codec, this);
		this.allowConditions = allowConditions;
	}

	/**
	 * Marks this power has having a ticking function, if this isn't done,
	 * the mod won't bother calling the {@link #tick(ConfiguredPower, Entity)} function.
	 *
	 * @param whenInactive If true, tick will bypass the check to {@link #isActive(ConfiguredPower, Entity)}
	 * @see #ticking() for a version that sets whenInactive to false.
	 */
	protected final void ticking(boolean whenInactive) {
		this.ticking = true;
		this.tickingWhenInactive = whenInactive;
	}

	public MapCodec<ConfiguredPower<T, ?>> getCodec() {
		return this.codec;
	}

	/**
	 * Marks this power has having a ticking function, if this isn't done,
	 * the mod won't bother calling the {@link #tick(ConfiguredPower, Entity)} function.
	 *
	 * @see #ticking(boolean) for a version that allows ticking when this power is inactive.
	 */
	protected final void ticking() {
		this.ticking(false);
	}

	/**
	 * Returns a map containing children of this power.<br/>
	 * Apoli uses this for the "multiple" power type, which contains children.<br/>
	 * Should only be used during loading as subpowers to provide subpowers
	 *
	 * @param configuration The configuration of this power.
	 * @return A map containing children of this power.
	 */
	public Map<String, Holder<ConfiguredPower<?, ?>>> getContainedPowers(ConfiguredPower<T, ?> configuration) {
		return ImmutableMap.of();
	}

	public Set<ResourceKey<ConfiguredPower<?, ?>>> getContainedPowerKeys(ConfiguredPower<T, ?> configuration) {
		Map<String, Holder<ConfiguredPower<?, ?>>> contained = this.getContainedPowers(configuration);
		if (contained.isEmpty())
			return ImmutableSet.of();
		ResourceLocation key = configuration.getRegistryName();
		if (key == null) {
			Apoli.LOGGER.error("Cannot access contained keys as this power is unregistered: {}", configuration);
			return ImmutableSet.of();
		}
		return contained.keySet().stream().map(suffix -> ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getPath() + suffix))).collect(Collectors.toUnmodifiableSet());
	}

	public ConfiguredPower<T, ?> configure(T input, PowerData data) {
		return new ConfiguredPower<>(() -> this, input, data);
	}

	protected boolean shouldCheckConditions(ConfiguredPower<T, ?> configuration, Entity entity) {
		return this.allowConditions;
	}

	protected boolean shouldTickWhenActive(ConfiguredPower<T, ?> configuration, Entity entity) {
		return this.ticking;
	}

	protected boolean shouldTickWhenInactive(ConfiguredPower<T, ?> configuration, Entity entity) {
		return this.tickingWhenInactive;
	}

	public boolean canTick(ConfiguredPower<T, ?> configuration, Entity entity) {
		return this.shouldTickWhenActive(configuration, entity) && (this.shouldTickWhenInactive(configuration, entity) || this.isActive(configuration, entity));
	}

	public void tick(ConfiguredPower<T, ?> configuration, Entity entity) {
		this.tick(configuration.getConfiguration(), entity);
	}

	public void onGained(ConfiguredPower<T, ?> configuration, Entity entity) {
		this.onGained(configuration.getConfiguration(), entity);
	}

	public void onLost(ConfiguredPower<T, ?> configuration, Entity entity) {
		this.onLost(configuration.getConfiguration(), entity);
	}

	public void onAdded(ConfiguredPower<T, ?> configuration, Entity entity) {
		this.onAdded(configuration.getConfiguration(), entity);
	}

	public void onRemoved(ConfiguredPower<T, ?> configuration, Entity entity) {
		this.onRemoved(configuration.getConfiguration(), entity);
	}

	public void onRespawn(ConfiguredPower<T, ?> configuration, Entity entity) {
		this.onRespawn(configuration.getConfiguration(), entity);
	}

	public int tickInterval(ConfiguredPower<T, ?> configuration, Entity entity) {
		return this.tickInterval(configuration.getConfiguration(), entity);
	}

	protected void tick(T configuration, Entity entity) {
	}

	protected void onGained(T configuration, Entity entity) {
	}

	protected void onLost(T configuration, Entity entity) {
	}

	protected void onAdded(T configuration, Entity entity) {
	}

	protected void onRemoved(T configuration, Entity entity) {
	}

	protected void onRespawn(T configuration, Entity entity) {
	}

	protected int tickInterval(T configuration, Entity entity) {
		return 1;
	}

	public boolean isActive(ConfiguredPower<T, ?> configuration, Entity entity) {
		return !this.shouldCheckConditions(configuration, entity) || configuration.getData().conditions().stream().allMatch(condition -> condition.check(entity));
	}

	/**
	 * Returns the codec used for additional data serialization.
	 */
	public @Nullable Codec<?> getDataCodec() {
		return null;
	}

	public void serialize(ConfiguredPower<T, ?> configuration, PowerContainer container, CompoundTag tag) {
	}

	public void deserialize(ConfiguredPower<T, ?> configuration, PowerContainer container, CompoundTag tag) {
	}

	private final Lazy<io.github.apace100.apoli.power.factory.PowerFactory<?>> legacyType = Lazy.of(() -> new io.github.apace100.apoli.power.factory.PowerFactory<>(ApoliRegistries.POWER_FACTORY.getKey(this), this));

	public io.github.apace100.apoli.power.factory.PowerFactory<?> getLegacyFactory() {
		return this.legacyType.get();
	}

	public T complete(ResourceLocation identifier, T configuration) {
		return configuration;
	}
}
