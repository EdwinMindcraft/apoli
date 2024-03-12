package io.github.edwinmindcraft.apoli.api.power.configuration;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.apoli.api.power.IHudRenderedPower;
import io.github.edwinmindcraft.apoli.api.power.IVariableIntPower;
import io.github.edwinmindcraft.apoli.api.power.PowerData;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import io.github.edwinmindcraft.calio.api.registry.DynamicRegistryListener;
import io.github.edwinmindcraft.calio.api.registry.ICalioDynamicRegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * This is a replacement for the power system used by fabric.
 * It should be better when it comes to memory usage since every power is only instantiated once.
 * The main drawback is when it comes to mutable variables, in which case this system feels wrong
 * to use, although consider using provided types, as they (mostly) hide all this in simple cases.
 *
 * @param <C> The type of the configuration.
 * @param <F> The type of the factory.
 */
public final class ConfiguredPower<C extends IDynamicFeatureConfiguration, F extends PowerFactory<C>> extends AttachmentHolder implements IDynamicFeatureConfiguration, DynamicRegistryListener {
	public static final Codec<ConfiguredPower<?, ?>> CODEC = PowerFactory.CODEC.dispatch(ConfiguredPower::getFactory, PowerFactory::getCodec);
	public static final CodecSet<ConfiguredPower<?, ?>> CODEC_SET = CalioCodecHelper.forDynamicRegistry(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, SerializableDataTypes.IDENTIFIER, CODEC);
	public static final Codec<Holder<ConfiguredPower<?, ?>>> HOLDER = CODEC_SET.holder();

	public static MapCodec<Holder<ConfiguredPower<?, ?>>> required(String name) {
		return HOLDER.fieldOf(name);
	}

	public static MapCodec<Optional<Holder<ConfiguredPower<?, ?>>>> optional(String name) {
		return CalioCodecHelper.optionalField(HOLDER, name);
	}

	private final Lazy<F> factory;
	private final C configuration;
	private final PowerData data;

	private final Supplier<F> factorySupplier;

	public ConfiguredPower(Supplier<F> factory, C configuration, PowerData data) {
		this(null, factory, configuration, data);
	}

	private ConfiguredPower(@Nullable ResourceLocation key, Supplier<F> factory, C configuration, PowerData data) {
		this.registryName = key;
		this.configuration = configuration;
		this.data = data;
		this.factorySupplier = factory;
		this.factory = Lazy.of(factory);
		if (factory instanceof Holder<?> holder && holder.isBound())
			this.factory.get(); //Should be mostly safe.
	}

	public PowerData getData() {
		return this.data;
	}

	/**
	 * Returns a list of the subpowers contained by this power, as well as the suffix associated
	 * with that subpower.<br/>
	 * Should only be used during loading as subpowers to provide subpowers
	 *
	 * @see #getContainedPowerKeys()
	 */
	public Map<String, Holder<ConfiguredPower<?, ?>>> getContainedPowers() {
		return this.getFactory().getContainedPowers(this);
	}

	public Set<ResourceKey<ConfiguredPower<?, ?>>> getContainedPowerKeys() {
		return this.getFactory().getContainedPowerKeys(this);
	}

	public boolean isActive(Entity entity) {
		return this.getConfiguration().isConfigurationValid() && this.getFactory().isActive(this, entity);
	}

	public void onGained(Entity entity) {
		this.getFactory().onGained(this, entity);
	}

	public void onRemoved(Entity entity) {
		this.getFactory().onRemoved(this, entity);
	}

	public void onLost(Entity entity) {
		this.getFactory().onLost(this, entity);
	}

	public void onAdded(Entity entity) {
		this.getFactory().onAdded(this, entity);
	}

	public void onRespawn(Entity entity) {
		this.getFactory().onRespawn(this, entity);
	}

	public <T> T getPowerData(Entity player, Supplier<@NotNull T> supplier) {
		PowerContainer container = PowerContainer.get(player);
		return container != null ? this.getPowerData(container, supplier) : supplier.get();
	}

	public <T> T getPowerData(PowerContainer container, Supplier<@NotNull T> supplier) {
		if (this.registryName == null)
			return container.getPowerData(Holder.direct(this), supplier);
		else
			return container.getPowerData(ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, this.registryName), supplier);
	}

	/**
	 * Accesses a list of all children, recursively.<br>
	 * This allows for nested multiple powers, although this isn't a feature
	 * of the fabric version, as such I would recommend using it if you want
	 * to maintain compatibility between versions.<br/>
	 * Should only be used during loading as subpowers to provide subpowers
	 *
	 * @see #getChildrenKeys()
	 */
	public Set<Holder<ConfiguredPower<?, ?>>> getChildren() {
		ImmutableSet.Builder<Holder<ConfiguredPower<?, ?>>> builder = ImmutableSet.builder();
		this.getContainedPowers().values().forEach(value -> {
			if (value.isBound())
				builder.add(value).addAll(value.value().getChildren());
		});
		return builder.build();
	}

	/**
	 * Accesses a list of all children, recursively.<br>
	 * This allows for nested multiple powers, although this isn't a feature
	 * of the fabric version, as such I would recommend using it if you want
	 * to maintain compatibility between versions.
	 */
	public Set<ResourceKey<ConfiguredPower<?, ?>>> getChildrenKeys() {
		Registry<ConfiguredPower<?, ?>> registry = ApoliAPI.getPowers();
		ImmutableSet.Builder<ResourceKey<ConfiguredPower<?, ?>>> builder = ImmutableSet.builder();
		for (ResourceKey<ConfiguredPower<?, ?>> value : this.getContainedPowerKeys()) {
			builder.add(value);
			registry.getHolder(value).filter(Holder::isBound).ifPresent(x -> builder.addAll(x.value().getChildrenKeys()));
		}
		return builder.build();
	}

	public CompoundTag serialize(PowerContainer container) {
		CompoundTag tag = new CompoundTag();
		CompoundTag attachments = this.serializeAttachments();
		if (attachments != null && !attachments.isEmpty())
			tag.put("neoforge:attachments", attachments);
		this.getFactory().serialize(this, container, tag);
		return tag;
	}

	public void deserialize(PowerContainer container, CompoundTag tag) {
		if (tag.contains("neoforge:attachments", Tag.TAG_COMPOUND))
			this.deserializeAttachments(tag.getCompound("neoforge:attachments"));
		this.getFactory().deserialize(this, container, tag);
	}

	/**
	 * Executes a tick of the current factory if it is eligible to.<br/>
	 * You cannot force a tick of a non-ticking power.
	 *
	 * @param entity The entity to execute the action on.
	 * @param force  If true, there won't be any check to {@link PowerFactory#tickInterval(ConfiguredPower, Entity)}.
	 * @see #tick(Entity) for a version without the ability to be forced.
	 */
	public void tick(Entity entity, boolean force) {
		if (this.getFactory().canTick(this, entity)) {
			if (!force) {
				int i = this.getFactory().tickInterval(this, entity);
				if (i <= 0 || (entity.tickCount % i) != 0)
					return;
			}
			this.getFactory().tick(this, entity);
		}
	}

	public void tick(Entity entity) {
		this.tick(entity, false);
	}

	//VariableIntPower
	@SuppressWarnings("unchecked")
	public Optional<IVariableIntPower<C>> asVariableIntPower() {
		if (this.getFactory() instanceof IVariableIntPower<?> variableIntPower)
			return Optional.of((IVariableIntPower<C>) variableIntPower);
		return Optional.empty();
	}

	public OptionalInt assign(Entity entity, int value) {
		return this.asVariableIntPower().map(t -> t.assign(this, entity, value)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	public OptionalInt getValue(Entity entity) {
		return this.asVariableIntPower().map(t -> t.getValue(this, entity)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	public OptionalInt getMaximum(Entity entity) {
		return this.asVariableIntPower().map(t -> t.getMaximum(this, entity)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	public OptionalInt getMinimum(Entity entity) {
		return this.asVariableIntPower().map(t -> t.getMinimum(this, entity)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	public OptionalInt change(Entity entity, int amount) {
		return this.asVariableIntPower().map(t -> t.change(this, entity, amount)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	public OptionalInt increment(Entity entity) {
		return this.asVariableIntPower().map(t -> t.increment(this, entity)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	public OptionalInt decrement(Entity entity) {
		return this.asVariableIntPower().map(t -> t.decrement(this, entity)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	//Hud Rendered Power

	@SuppressWarnings("unchecked")
	public Optional<IHudRenderedPower<C>> asHudRendered() {
		return this.getFactory() instanceof IHudRenderedPower<?> hudRenderedPower ? Optional.of((IHudRenderedPower<C>) hudRenderedPower) : Optional.empty();
	}

	public Optional<HudRender> getRenderSettings(Entity entity) {
		return this.asHudRendered().map(x -> x.getRenderSettings(this, entity));
	}

	public Optional<Boolean> shouldRender(Entity entity) {
		return this.asHudRendered().map(x -> x.shouldRender(this, entity));
	}

	public Optional<Float> getFill(Entity entity) {
		return this.asHudRendered().map(x -> x.getFill(this, entity));
	}

	//Active Power

	@SuppressWarnings("unchecked")
	public Optional<IActivePower<C>> asActive() {
		return this.getFactory() instanceof IActivePower<?> hudRenderedPower ? Optional.of((IActivePower<C>) hudRenderedPower) : Optional.empty();
	}

	public boolean activate(Entity entity) {
		Optional<IActivePower<C>> ciActivePower = this.asActive();
		ciActivePower.ifPresent(x -> x.activate(this, entity));
		return ciActivePower.isPresent();
	}

	public Optional<IActivePower.Key> getKey(@Nullable Entity entity) {
		return this.asActive().map(x -> x.getKey(this, entity));
	}

	public ConfiguredPower<C, F> complete(ResourceLocation name) {
		return new ConfiguredPower<>(name, this.factorySupplier, this.factorySupplier.get().complete(name, this.getConfiguration()), this.getData().complete(name));
	}

	public F getFactory() {
		return this.factory.get();
	}

	public C getConfiguration() {
		return this.configuration;
	}

	@Override
	public @NotNull List<String> getErrors(@NotNull ICalioDynamicRegistryManager server) {
		return this.getConfiguration().getErrors(server);
	}

	@Override
	public @NotNull List<String> getWarnings(@NotNull ICalioDynamicRegistryManager server) {
		return this.getConfiguration().getWarnings(server);
	}

	@Override
	public boolean isConfigurationValid() {
		return this.getConfiguration().isConfigurationValid();
	}

	@Override
	public String toString() {
		String str = "CP:" + ApoliRegistries.POWER_FACTORY.getKey(this.getFactory()) + "(" + this.getData() + ")-" + this.getConfiguration();
		return this.registryName != null ? "[" + this.registryName + "]" + str : str;
	}

	private final Lazy<PowerType<?>> type = Lazy.of(() -> new PowerType<>(this));

	public PowerType<?> getPowerType() {
		return this.type.get();
	}

	//Necessary to uniquely identify each power.
	private ResourceLocation registryName;

	@Override
	public int hashCode() {
		return this.registryName != null ? this.registryName.hashCode() : Objects.hash(this.factory, this.configuration, this.data);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ConfiguredPower<?, ?> other))
			return false;
		if (this.registryName != null && other.registryName != null)
			return Objects.equals(this.registryName, other.registryName);
		return Objects.equals(this.factorySupplier.get(), other.factorySupplier.get()) &&
				Objects.equals(this.configuration, other.configuration) &&
				Objects.equals(this.data, other.data);
	}

	@Nullable
	public ResourceLocation getRegistryName() {
		if (this.registryName != null)
			return this.registryName;
		return ApoliAPI.getPowers().getResourceKey(this).map(ResourceKey::location).orElse(null);
	}

	@Override
	public void whenAvailable(@NotNull ICalioDynamicRegistryManager manager) {
		if (this.getFactory() instanceof DynamicRegistryListener drl)
			drl.whenAvailable(manager);
	}

	@Override
	public void whenNamed(@NotNull ResourceLocation name) {
		if (this.registryName == null)
			this.registryName = name;
		else if (!Objects.equals(this.registryName, name))
			throw new IllegalArgumentException("Tried to assign name \"" + name + "\" to power with name \"" + this.registryName + "\"");
		if (this.getFactory() instanceof DynamicRegistryListener drl)
			drl.whenNamed(name);
	}
}
