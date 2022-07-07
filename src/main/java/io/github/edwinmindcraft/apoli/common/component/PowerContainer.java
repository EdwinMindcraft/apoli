package io.github.edwinmindcraft.apoli.common.component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.util.GainedPowerCriterion;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.registry.ApoliCapabilities;
import io.github.edwinmindcraft.calio.api.CalioAPI;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PowerContainer implements IPowerContainer, ICapabilitySerializable<Tag> {
	@NotNull
	private final LivingEntity owner;
	private final Map<ResourceKey<ConfiguredPower<?, ?>>, Holder<ConfiguredPower<?, ?>>> powers;
	private final Map<ResourceKey<ConfiguredPower<?, ?>>, Set<ResourceLocation>> powerSources;
	private final Map<ResourceKey<ConfiguredPower<?, ?>>, Object> powerData;
	private transient final LazyOptional<IPowerContainer> thisOptional = LazyOptional.of(() -> this);

	public PowerContainer(@NotNull LivingEntity owner) {
		this.owner = owner;
		this.powers = new ConcurrentHashMap<>();
		this.powerSources = new ConcurrentHashMap<>();
		this.powerData = new ConcurrentHashMap<>();
	}

	@Override
	public void removePower(ResourceKey<ConfiguredPower<?, ?>> power, ResourceLocation source) {
		if (this.powerSources.containsKey(power)) {
			Set<ResourceLocation> sources = this.powerSources.get(power);
			sources.remove(source);
			Holder<ConfiguredPower<?, ?>> instance = this.powers.get(power);
			if (sources.isEmpty()) {
				this.powerSources.remove(power);
				this.powers.remove(power);
				if (instance != null && instance.isBound()) {
					instance.value().onRemoved(this.owner);
					instance.value().onLost(this.owner);
				}
			}
			if (instance != null && instance.isBound()) {
				Registry<ConfiguredPower<?, ?>> powerRegistry = ApoliAPI.getPowers();
				for (Holder<ConfiguredPower<?, ?>> value : instance.value().getContainedPowers().values())
					value.unwrap().map(Optional::of, powerRegistry::getResourceKey).ifPresent(id -> this.removePower(id, source));
			}
		}
	}

	@Override
	public int removeAllPowersFromSource(ResourceLocation source) {
		List<ResourceKey<ConfiguredPower<?, ?>>> powersFromSource = this.getPowersFromSource(source);
		powersFromSource.forEach(power -> this.removePower(power, source));
		return powersFromSource.size();
	}

	@Override
	public @NotNull List<ResourceKey<ConfiguredPower<?, ?>>> getPowersFromSource(ResourceLocation source) {
		return this.powerSources.entrySet().stream().filter(x -> x.getValue().contains(source)).map(Map.Entry::getKey).toList();
	}

	@Override
	public boolean addPower(ResourceKey<ConfiguredPower<?, ?>> power, ResourceLocation source) {
		Registry<ConfiguredPower<?, ?>> powers = ApoliAPI.getPowers(this.owner.getServer());
		Optional<Holder<ConfiguredPower<?, ?>>> optionalInstance = powers.getHolder(power);
		if (optionalInstance.isEmpty() || !optionalInstance.get().isBound()) {
			Apoli.LOGGER.error("Trying to add unregistered power {} to entity {}", power, this.owner);
			return false;
		}
		Holder<ConfiguredPower<?, ?>> instance = optionalInstance.get();
		if (this.powerSources.containsKey(power)) {
			Set<ResourceLocation> sources = this.powerSources.get(power);
			if (sources.contains(source)) {
				return false;
			} else {
				sources.add(source);
				for (Holder<ConfiguredPower<?, ?>> value : instance.value().getContainedPowers().values()) {
					if (value.isBound()) {
						value.unwrap().map(Optional::of, powers::getResourceKey).ifPresent(id -> {
							Apoli.LOGGER.info("Adding subpower {} from power {}", id, power);
							this.addPower(id, source);
						});
					}
				}
				return true;
			}
		} else {
			for (Holder<ConfiguredPower<?, ?>> value : instance.value().getContainedPowers().values()) {
				if (value.isBound()) {
					value.unwrap().map(Optional::of, powers::getResourceKey).ifPresent(id -> {
						Apoli.LOGGER.info("Adding subpower {} from power {}", id, power);
						this.addPower(id, source);
					});
				}
			}
			Set<ResourceLocation> sources = new HashSet<>();
			sources.add(source);
			this.powerSources.put(power, sources);
			this.powers.put(power, instance);
			instance.value().onGained(this.owner);
			instance.value().onAdded(this.owner);
			if (this.owner instanceof ServerPlayer spe)
				instance.unwrap().map(Optional::of, powers::getResourceKey).ifPresent(key -> GainedPowerCriterion.INSTANCE.trigger(spe, key));
			return true;
		}
	}

	@Override
	public boolean hasPower(@Nullable ResourceKey<ConfiguredPower<?, ?>> power) {
		return power != null && this.powers.containsKey(power);
	}

	@Override
	public boolean hasPower(ResourceKey<ConfiguredPower<?, ?>> power, ResourceLocation source) {
		return this.powerSources.containsKey(power) && this.powerSources.get(power).contains(source);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public @Nullable <C extends IDynamicFeatureConfiguration, F extends PowerFactory<C>> Holder<ConfiguredPower<C, F>> getPower(ResourceKey<ConfiguredPower<?, ?>> power) {
		Holder<ConfiguredPower<?, ?>> holder = this.powers.get(power);
		if (holder != null && holder.isBound())
			return (Holder<ConfiguredPower<C, F>>) (Holder) holder;
		return null;
	}

	@Override
	public @NotNull List<Holder<ConfiguredPower<?, ?>>> getPowers() {
		return this.powers.values().stream().filter(Holder::isBound).collect(ImmutableList.toImmutableList());
	}

	@Override
	public @NotNull Set<ResourceKey<ConfiguredPower<?, ?>>> getPowerTypes(boolean includeSubPowers) {
		if (includeSubPowers)
			return ImmutableSet.copyOf(this.powers.keySet());
		Registry<ConfiguredPower<?, ?>> powers = ApoliAPI.getPowers(this.owner.getServer());
		Set<ResourceKey<ConfiguredPower<?, ?>>> subPowers = this.powers.entrySet().stream().flatMap(x -> x.getValue().value().getChildren().stream().filter(Holder::isBound).map(Holder::value).map(powers::getResourceKey).flatMap(Optional::stream)).collect(Collectors.toUnmodifiableSet());
		return this.powers.keySet().stream().filter(x -> !subPowers.contains(x)).collect(Collectors.toUnmodifiableSet());
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public @NotNull <C extends IDynamicFeatureConfiguration, F extends PowerFactory<C>> List<Holder<ConfiguredPower<C, F>>> getPowers(F factory, boolean includeInactive) {
		ImmutableList.Builder<Holder<ConfiguredPower<C, F>>> builder = ImmutableList.builder();
		this.powers.values().stream()
				.filter(Holder::isBound)
				.filter(value -> Objects.equals(factory, value.value().getFactory()) && (includeInactive || value.value().isActive(this.owner)))
				.map(value -> (Holder<ConfiguredPower<C, F>>) (Holder) value).forEach(builder::add);
		return builder.build();
	}

	@Override
	public @NotNull List<ResourceLocation> getSources(ResourceKey<ConfiguredPower<?, ?>> power) {
		return this.powerSources.containsKey(power) ? ImmutableList.copyOf(this.powerSources.get(power)) : ImmutableList.of();
	}

	@Override
	public void sync() {
		ApoliAPI.synchronizePowerContainer(this.owner);
	}

	@Override
	public void serverTick() {
		Iterator<Holder<ConfiguredPower<?, ?>>> iterator = this.powers.values().iterator();
		while (iterator.hasNext()) {
			Holder<ConfiguredPower<?, ?>> value = iterator.next();
			if (value.isBound())
				value.value().tick(this.owner);
			else
				iterator.remove();
		}
	}

	@Override
	public void readNbt(CompoundTag tag, boolean applyEvents) {
		try {
			if (applyEvents) {
				for (Holder<ConfiguredPower<?, ?>> power : this.powers.values()) {
					if (power.isBound()) {
						power.value().onRemoved(this.owner);
						power.value().onLost(this.owner);
					}
				}
			}
			this.powers.clear();
			this.powerSources.clear();
			this.powerData.clear();
			ListTag powerList = (ListTag) tag.get("Powers");
			Registry<ConfiguredPower<?, ?>> powers = ApoliAPI.getPowers();
			if (powerList != null) {
				for (int i = 0; i < powerList.size(); i++) {
					CompoundTag powerTag = powerList.getCompound(i);
					ResourceLocation typeKey = ResourceLocation.tryParse(powerTag.getString("Type"));
					if (typeKey == null) {
						Apoli.LOGGER.warn("Power key  \"" + powerTag.getString("Type") + "\" was not a valid identifier");
						continue;
					}
					ResourceKey<ConfiguredPower<?, ?>> identifier = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, typeKey);
					ListTag sources = (ListTag) powerTag.get("Sources");
					Set<ResourceLocation> list = new HashSet<>();
					if (sources != null)
						sources.forEach(nbtElement -> list.add(ResourceLocation.tryParse(nbtElement.getAsString())));
					this.powerSources.put(identifier, list);
					try {
						CompoundTag data = powerTag.getCompound("Data");
						Optional<Holder<ConfiguredPower<?, ?>>> optionalPower = powers.getHolder(identifier).filter(Holder::isBound);
						if (optionalPower.isEmpty()) {
							Apoli.LOGGER.warn("Power data of unregistered power \"" + identifier + "\" found on entity, skipping...");
							continue;
						}
						Holder<ConfiguredPower<?, ?>> instance = optionalPower.get();
						try {
							instance.value().deserialize(this, data);
						} catch (ClassCastException e) {
							Apoli.LOGGER.warn("Data type of \"" + identifier + "\" changed, skipping data for that power on entity " + this.owner.getName().getContents());
						}
						this.powers.put(identifier, instance);
						if (applyEvents)
							instance.value().onAdded(this.owner);
					} catch (IllegalArgumentException e) {
						Apoli.LOGGER.warn("Power data of unregistered power \"" + identifier + "\" found on entity, skipping...");
					}
				}
				for (Map.Entry<ResourceKey<ConfiguredPower<?, ?>>, Set<ResourceLocation>> entry : this.powerSources.entrySet()) {
					ConfiguredPower<?, ?> power = powers.get(entry.getKey());
					if (power == null) //This would take a miracle to occur.
						continue;
					for (Map.Entry<String, Holder<ConfiguredPower<?, ?>>> subPower : power.getContainedPowers().entrySet()) {
						subPower.getValue().unwrap().map(Optional::of, powers::getResourceKey).ifPresentOrElse(sub -> {
							for (ResourceLocation source : entry.getValue()) {
								if (!this.hasPower(sub, source))
									this.addPower(sub, source);
							}
						}, () -> Apoli.LOGGER.warn("Multiple power type read from data contained unregistered sub-type: \"" + entry.getKey() + subPower.getKey() + "\"."));
					}
				}
			}
		} catch (Exception e) {
			Apoli.LOGGER.info("Error while reading data: " + e.getMessage());
		}
	}

	@Override
	public void rebuildCache() { //Storing powers
		ImmutableSet<ResourceKey<ConfiguredPower<?, ?>>> powers = ImmutableSet.copyOf(this.powers.keySet());
		Registry<ConfiguredPower<?, ?>> registry = ApoliAPI.getPowers();
		for (ResourceKey<ConfiguredPower<?, ?>> power : powers) {
			Optional<Holder<ConfiguredPower<?, ?>>> holder = registry.getHolder(power).filter(Holder::isBound);
			if (holder.isPresent()) {
				this.powers.put(power, holder.get());
			} else {
				this.powerSources.get(power).forEach(source -> this.removePower(power, source)); //Safely remove powers while the previous is still in cache.
				Apoli.LOGGER.warn("Power {} was removed from entity {} as it doesn't exist anymore.", power, this.owner.getScoreboardName());
			}
		}
	}

	@Override
	public void handle(Multimap<ResourceLocation, ResourceLocation> powerSources, Map<ResourceLocation, CompoundTag> data) {
		this.powerSources.clear();
		this.powers.clear();
		this.powerData.clear();
		Registry<ConfiguredPower<?, ?>> powerRegistry = CalioAPI.getDynamicRegistries(this.owner.getServer()).get(ApoliDynamicRegistries.CONFIGURED_POWER_KEY);
		for (Map.Entry<ResourceLocation, Collection<ResourceLocation>> powerEntry : powerSources.asMap().entrySet()) {
			ResourceKey<ConfiguredPower<?, ?>> power = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, powerEntry.getKey());
			Optional<Holder<ConfiguredPower<?, ?>>> configuredPower = powerRegistry.getHolder(power).filter(Holder::isBound);
			if (configuredPower.isEmpty()) {
				Apoli.LOGGER.warn("Received missing power {} from server for entity {}", power, this.owner.getScoreboardName());
				continue;
			}
			this.powers.put(power, configuredPower.get());
			this.powerSources.put(power, new HashSet<>(powerEntry.getValue()));
			CompoundTag tag = data.get(powerEntry.getKey());
			if (tag != null)
				configuredPower.get().value().deserialize(this, tag);
		}
	}

	@Override
	public CompoundTag writeToNbt(CompoundTag tag) {
		ListTag powerList = new ListTag();
		for (Map.Entry<ResourceKey<ConfiguredPower<?, ?>>, Holder<ConfiguredPower<?, ?>>> powerEntry : this.powers.entrySet()) {
			CompoundTag powerTag = new CompoundTag();
			powerTag.putString("Type", powerEntry.getKey().location().toString());
			powerTag.put("Data", powerEntry.getValue().value().serialize(this));
			ListTag sources = new ListTag();
			this.powerSources.get(powerEntry.getKey()).forEach(id -> sources.add(StringTag.valueOf(id.toString())));
			powerTag.put("Sources", sources);
			powerList.add(powerTag);
		}
		tag.put("Powers", powerList);
		return tag;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> @NotNull T getPowerData(ResourceKey<ConfiguredPower<?, ?>> power, NonNullSupplier<? extends T> supplier) {
		Object obj = this.powerData.computeIfAbsent(power, x -> supplier.get());
		try {
			return (T) obj;
		} catch (ClassCastException e) {
			return (T) this.powerData.put(power, supplier.get());
		}
	}

	@Override
	public Entity getOwner() {
		return this.owner;
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return ApoliCapabilities.POWER_CONTAINER.orEmpty(cap, this.thisOptional);
	}

	@Override
	public Tag serializeNBT() {
		return this.writeToNbt(new CompoundTag());
	}

	@Override
	public void deserializeNBT(Tag nbt) {
		this.readFromNbt((CompoundTag) nbt);
	}
}
