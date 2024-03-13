package io.github.edwinmindcraft.apoli.api.power.factory;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.IFactory;
import io.github.edwinmindcraft.apoli.api.power.PowerData;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;

public abstract class PowerFactory<T extends IDynamicFeatureConfiguration> extends ForgeRegistryEntry<PowerFactory<?>> {
	public static final Codec<PowerFactory<?>> CODEC = ApoliRegistries.codec(ApoliRegistries.POWER_FACTORY);
	private static final Map<String, ResourceLocation> ALIASES = Util.make(() -> {
		ImmutableMap.Builder<String, ResourceLocation> builder = ImmutableMap.builder();
		try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(PowerFactory.class.getResourceAsStream("/data/apoli/power_class_registry.json")))) {
			Gson gson = new GsonBuilder().create();
			JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
			for (String s : jsonObject.keySet()) {
				builder.put(s, new ResourceLocation(jsonObject.get(s).getAsString()));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return builder.build();
	});

	private static <T extends IDynamicFeatureConfiguration, F extends PowerFactory<T>> Codec<ConfiguredPower<T, ?>> powerCodec(Codec<T> codec, F factory) {
		return IFactory.unionCodec(IFactory.asMap(codec), PowerData.CODEC, factory::configure, ConfiguredPower::getConfiguration, ConfiguredPower::getData);
	}

	public static final Codec<PowerFactory<?>> IGNORE_NAMESPACE_CODEC = Codec.STRING.comapFlatMap(val -> {
		if (val.startsWith("io.github.apace100.apoli.power."))
			val = val.substring("io.github.apace100.apoli.power.".length());
		ResourceLocation temp = ALIASES.get(val);
		ResourceLocation id = temp != null ? temp : ResourceLocation.tryParse(val);
		if (id == null)
			return DataResult.error("Failed to convert \"" + val + "\" to a resource location");
		PowerFactory<?> value = ApoliRegistries.POWER_FACTORY.get().getValue(id);
		if (value != null)
			return DataResult.success(value); //Avoid the slow code if we can.
		return ApoliRegistries.POWER_FACTORY.get().getEntries().stream()
				.filter(entry -> entry.getKey().location().getPath().equals(id.getPath()))
				.findFirst().map(Map.Entry::getValue)
				.map(DataResult::success)
				.orElseGet(() -> DataResult.error("Failed to find power factory with path: " + id.getPath()));
	}, x -> x.getRegistryName().toString());

	private final Codec<ConfiguredPower<T, ?>> codec;
	private final boolean allowConditions;
	private boolean ticking = false;
	private boolean tickingWhenInactive = false;

	protected PowerFactory(Codec<T> codec) {
		this(codec, true);
	}

	/**
	 * Creates a new power factory.
	 *
	 * @param codec           The codec used to serialize the configuration of this power.
	 * @param allowConditions Determines whether this power will use the global field {@link PowerData#conditions()} or not.
	 *
	 * @see #PowerFactory(Codec) for a version with allow conditions true by default.
	 */
	protected PowerFactory(Codec<T> codec, boolean allowConditions) {
		this.codec = powerCodec(codec, this);
		this.allowConditions = allowConditions;
	}

	/**
	 * Marks this power has having a ticking function, if this isn't done,
	 * the mod won't bother calling the {@link #tick(ConfiguredPower, Entity)} function.
	 *
	 * @param whenInactive If true, tick will bypass the check to {@link #isActive(ConfiguredPower, Entity)}
	 *
	 * @see #ticking() for a version that sets whenInactive to false.
	 */
	protected final void ticking(boolean whenInactive) {
		this.ticking = true;
		this.tickingWhenInactive = whenInactive;
	}

	public Codec<ConfiguredPower<T, ?>> getCodec() {
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
	 * Apoli uses this for the "multiple" power type, which contains children.
	 *
	 * @param configuration The configuration of this power.
	 *
	 * @return A map containing children of this power.
	 */
	public Map<String, Holder<ConfiguredPower<?, ?>>> getContainedPowers(ConfiguredPower<T, ?> configuration) {
		return ImmutableMap.of();
	}

	@Nullable
	public ICapabilityProvider initCapabilities() {
		return null;
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

	protected void tick(T configuration, Entity entity) {}

	protected void onGained(T configuration, Entity entity) {}

	protected void onLost(T configuration, Entity entity) {}

	protected void onAdded(T configuration, Entity entity) {}

	protected void onRemoved(T configuration, Entity entity) {}

	protected void onRespawn(T configuration, Entity entity) {}

	protected int tickInterval(T configuration, Entity entity) {return 1;}

	public boolean isActive(ConfiguredPower<T, ?> configuration, Entity entity) {
		return !this.shouldCheckConditions(configuration, entity) || configuration.getData().conditions().stream().allMatch(condition -> condition.check(entity));
	}

	public void serialize(ConfiguredPower<T, ?> configuration, IPowerContainer container, CompoundTag tag) {}

	public void deserialize(ConfiguredPower<T, ?> configuration, IPowerContainer container, CompoundTag tag) {}

	private final Lazy<io.github.apace100.apoli.power.factory.PowerFactory<?>> legacyType = Lazy.of(() -> new io.github.apace100.apoli.power.factory.PowerFactory<>(this.getRegistryName(), this));

	public io.github.apace100.apoli.power.factory.PowerFactory<?> getLegacyFactory() {
		return this.legacyType.get();
	}

	public T complete(ResourceLocation identifier, T configuration) {
		return configuration;
	}
}
