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
	private final Map<ResourceLocation, ConfiguredPower<?, ?>> powers;
	private final Map<ResourceLocation, Set<ResourceLocation>> powerSources;
	private final Map<ResourceLocation, Object> powerData;
	private transient final LazyOptional<IPowerContainer> thisOptional = LazyOptional.of(() -> this);

	public PowerContainer(@NotNull LivingEntity owner) {
		this.owner = owner;
		this.powers = new ConcurrentHashMap<>();
		this.powerSources = new ConcurrentHashMap<>();
		this.powerData = new ConcurrentHashMap<>();
	}

	@Override
	public void removePower(ResourceLocation power, ResourceLocation source) {
		if (this.powerSources.containsKey(power)) {
			Set<ResourceLocation> sources = this.powerSources.get(power);
			sources.remove(source);
			ConfiguredPower<?, ?> instance = this.powers.get(power);
			if (sources.isEmpty()) {
				this.powerSources.remove(power);
				this.powers.remove(power);
				if (instance != null) {
					instance.onRemoved(this.owner);
					instance.onLost(this.owner);
				}
			}
			if (instance != null) {
				for (Holder<ConfiguredPower<?, ?>> value : instance.getContainedPowers().values()) {
					ResourceLocation id = value.isBound() ? value.value().getRegistryName() : null;
					if (id != null)
						this.removePower(id, source);
				}
			}
		}
	}

	@Override
	public int removeAllPowersFromSource(ResourceLocation source) {
		List<ResourceLocation> powersFromSource = this.getPowersFromSource(source);
		powersFromSource.forEach(power -> this.removePower(power, source));
		return powersFromSource.size();
	}

	@Override
	public @NotNull List<ResourceLocation> getPowersFromSource(ResourceLocation source) {
		return this.powerSources.entrySet().stream().filter(x -> x.getValue().contains(source)).map(Map.Entry::getKey).toList();
	}

	@Override
	public boolean addPower(ResourceLocation power, ResourceLocation source) {
		Registry<ConfiguredPower<?, ?>> powers = ApoliAPI.getPowers(this.owner.getServer());
		Optional<ConfiguredPower<?, ?>> optionalInstance = powers.getOptional(power);
		if (optionalInstance.isEmpty()) {
			Apoli.LOGGER.error("Trying to add unregistered power {} to entity {}", power, this.owner);
			return false;
		}
		ConfiguredPower<?, ?> instance = optionalInstance.get();
		if (this.powerSources.containsKey(power)) {
			Set<ResourceLocation> sources = this.powerSources.get(power);
			if (sources.contains(source)) {
				return false;
			} else {
				sources.add(source);
				for (Holder<ConfiguredPower<?, ?>> value : instance.getContainedPowers().values()) {
					ResourceLocation id = value.isBound() ? value.value().getRegistryName() : null;
					Apoli.LOGGER.info("Adding subpower {} from power {}", id, power);
					if (id != null)
						this.addPower(id, source);
				}
				return true;
			}
		} else {
			for (Holder<ConfiguredPower<?, ?>> value : instance.getContainedPowers().values()) {
				ResourceLocation id = value.isBound() ? value.value().getRegistryName() : null;
				Apoli.LOGGER.info("Adding subpower {} from power {}", id, power);
				if (id != null)
					this.addPower(id, source);
			}
			Set<ResourceLocation> sources = new HashSet<>();
			sources.add(source);
			this.powerSources.put(power, sources);
			this.powers.put(power, instance);
			instance.onGained(this.owner);
			instance.onAdded(this.owner);
			if (this.owner instanceof ServerPlayer spe)
				GainedPowerCriterion.INSTANCE.trigger(spe, instance);
			return true;
		}
	}

	@Override
	public boolean hasPower(@Nullable ResourceLocation power) {
		return power != null && this.powers.containsKey(power);
	}

	@Override
	public boolean hasPower(ResourceLocation power, ResourceLocation source) {
		return this.powerSources.containsKey(power) && this.powerSources.get(power).contains(source);
	}

	@Override
	@SuppressWarnings("unchecked")
	public @Nullable <C extends IDynamicFeatureConfiguration, F extends PowerFactory<C>> ConfiguredPower<C, F> getPower(ResourceLocation power) {
		if (this.powers.containsKey(power))
			return (ConfiguredPower<C, F>) this.powers.get(power);
		return null;
	}

	@Override
	public @NotNull List<ConfiguredPower<?, ?>> getPowers() {
		return ImmutableList.copyOf(this.powers.values());
	}

	@Override
	public @NotNull Set<ResourceLocation> getPowerNames() {
		return ImmutableSet.copyOf(this.powers.keySet());
	}

	@Override
	public @NotNull Set<ResourceLocation> getPowerTypes(boolean includeSubPowers) {
		if (includeSubPowers)
			return ImmutableSet.copyOf(this.powers.keySet());
		Registry<ConfiguredPower<?, ?>> powers = ApoliAPI.getPowers(this.owner.getServer());
		Set<ResourceLocation> subPowers = this.powers.entrySet().stream().flatMap(x -> x.getValue().getChildren().stream().filter(Holder::isBound).map(Holder::value).map(powers::getKey).filter(Objects::nonNull)).collect(Collectors.toUnmodifiableSet());
		return this.powers.keySet().stream().filter(x -> !subPowers.contains(x)).collect(Collectors.toUnmodifiableSet());
	}

	@Override
	@SuppressWarnings("unchecked")
	public @NotNull <C extends IDynamicFeatureConfiguration, F extends PowerFactory<C>> List<ConfiguredPower<C, F>> getPowers(F factory, boolean includeInactive) {
		ImmutableList.Builder<ConfiguredPower<C, F>> builder = ImmutableList.builder();
		this.powers.values().stream()
				.filter(value -> Objects.equals(factory, value.getFactory()) && (includeInactive || value.isActive(this.owner)))
				.map(value -> (ConfiguredPower<C, F>) value).forEach(builder::add);
		return builder.build();
	}

	@Override
	public @NotNull List<ResourceLocation> getSources(ResourceLocation power) {
		return this.powerSources.containsKey(power) ? ImmutableList.copyOf(this.powerSources.get(power)) : ImmutableList.of();
	}

	@Override
	public void sync() {
		ApoliAPI.synchronizePowerContainer(this.owner);
	}

	@Override
	public void serverTick() {
		for (ConfiguredPower<?, ?> value : this.powers.values()) {
			value.tick(this.owner);
		}
	}

	@Override
	public void readNbt(CompoundTag tag, boolean applyEvents) {
		try {
			if (applyEvents) {
				for (ConfiguredPower<?, ?> power : this.powers.values()) {
					power.onRemoved(this.owner);
					power.onLost(this.owner);
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
					ResourceLocation identifier = ResourceLocation.tryParse(powerTag.getString("Type"));
					ListTag sources = (ListTag) powerTag.get("Sources");
					Set<ResourceLocation> list = new HashSet<>();
					if (sources != null)
						sources.forEach(nbtElement -> list.add(ResourceLocation.tryParse(nbtElement.getAsString())));
					this.powerSources.put(identifier, list);
					try {
						CompoundTag data = powerTag.getCompound("Data");
						Optional<ConfiguredPower<?, ?>> optionalPower = powers.getOptional(identifier);
						if (optionalPower.isEmpty()) {
							Apoli.LOGGER.warn("Power data of unregistered power \"" + identifier + "\" found on entity, skipping...");
							continue;
						}
						ConfiguredPower<?, ?> instance = optionalPower.get();
						try {
							instance.deserialize(this, data);
						} catch (ClassCastException e) {
							Apoli.LOGGER.warn("Data type of \"" + identifier + "\" changed, skipping data for that power on entity " + this.owner.getName().getContents());
						}
						this.powers.put(identifier, instance);
						if (applyEvents)
							instance.onAdded(this.owner);
					} catch (IllegalArgumentException e) {
						Apoli.LOGGER.warn("Power data of unregistered power \"" + identifier + "\" found on entity, skipping...");
					}
				}
				for (Map.Entry<ResourceLocation, Set<ResourceLocation>> entry : this.powerSources.entrySet()) {
					ConfiguredPower<?, ?> power = powers.get(entry.getKey());
					if (power == null) //This would take a miracle to occur.
						continue;
					for (Map.Entry<String, Holder<ConfiguredPower<?, ?>>> subPower : power.getContainedPowers().entrySet()) {
						ResourceLocation sub = subPower.getValue().isBound() ? subPower.getValue().value().getRegistryName() : null;
						if (sub == null) {
							Apoli.LOGGER.warn("Multiple power type read from data contained unregistered sub-type: \"" + entry.getKey() + subPower.getKey() + "\".");
							continue;
						}
						for (ResourceLocation source : entry.getValue()) {
							if (!this.hasPower(sub, source))
								this.addPower(sub, source);
						}
					}
				}
			}
		} catch (Exception e) {
			Apoli.LOGGER.info("Error while reading data: " + e.getMessage());
		}
	}

	@Override
	public void rebuildCache() { //Storing powers
		ImmutableSet<ResourceLocation> powers = ImmutableSet.copyOf(this.powers.keySet());
		Registry<ConfiguredPower<?, ?>> registry = ApoliAPI.getPowers();
		for (ResourceLocation power : powers) {
			if (registry.containsKey(power)) {
				this.powers.put(power, registry.get(power));
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
			ResourceLocation power = powerEntry.getKey();
			ConfiguredPower<?, ?> configuredPower = powerRegistry.get(power);
			if (configuredPower == null) {
				Apoli.LOGGER.warn("Received missing power {} from server for entity {}", power, this.owner.getScoreboardName());
				continue;
			}
			this.powers.put(power, configuredPower);
			this.powerSources.put(power, new HashSet<>(powerEntry.getValue()));
			CompoundTag tag = data.get(power);
			if (tag != null)
				configuredPower.deserialize(this, tag);
		}
	}

	@Override
	public CompoundTag writeToNbt(CompoundTag tag) {
		ListTag powerList = new ListTag();
		for (Map.Entry<ResourceLocation, ConfiguredPower<?, ?>> powerEntry : this.powers.entrySet()) {
			CompoundTag powerTag = new CompoundTag();
			powerTag.putString("Type", powerEntry.getKey().toString());
			powerTag.put("Data", powerEntry.getValue().serialize(this));
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
	public <T> @NotNull T getPowerData(ConfiguredPower<?, ?> power, NonNullSupplier<? extends T> supplier) {
		Object obj = this.powerData.computeIfAbsent(power.getRegistryName(), x -> supplier.get());
		try {
			return (T) obj;
		} catch (ClassCastException e) {
			return (T) this.powerData.put(power.getRegistryName(), supplier.get());
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
