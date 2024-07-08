package io.github.edwinmindcraft.apoli.common.component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.global.GlobalPowerSetUtil;
import io.github.apace100.apoli.util.GainedPowerCriterion;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.registry.ApoliCapabilities;
import io.github.edwinmindcraft.calio.api.CalioAPI;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
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

import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PowerContainerImpl implements PowerContainer, INBTSerializable<Tag> {
	@NotNull
	private final LivingEntity owner;
	private final Map<ResourceKey<ConfiguredPower<?, ?>>, Holder<ConfiguredPower<?, ?>>> powers;
	private final Map<ResourceKey<ConfiguredPower<?, ?>>, Set<ResourceLocation>> powerSources;
	private final Map<ResourceKey<ConfiguredPower<?, ?>>, Object> powerData;
	private final Map<PowerFactory<?>, List<Holder<ConfiguredPower<?, ?>>>> factoryAccessCache;

	public PowerContainerImpl(@NotNull LivingEntity owner) {
		this.owner = owner;
		this.powers = new ConcurrentHashMap<>();
		this.powerSources = new ConcurrentHashMap<>();
		this.powerData = new ConcurrentHashMap<>();
		this.factoryAccessCache = new ConcurrentHashMap<>();
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
				//If we can't remove the power, we'll rebuild the factory access cache.
				//This is slower, but still comparably faster than filtering holders
				if (instance != null && instance.isBound()) {
					PowerFactory<?> factory = instance.value().getFactory();
					instance.value().onRemoved(this.owner);
					instance.value().onLost(this.owner);
					if (this.factoryAccessCache.containsKey(factory))
						this.factoryAccessCache.get(factory).remove(instance);
					else
						this.rebuildFactoryAccessCache();
				} else
					this.rebuildFactoryAccessCache();
			}
			if (instance != null && instance.isBound()) {
				for (ResourceKey<ConfiguredPower<?, ?>> value : instance.value().getContainedPowerKeys())
					this.removePower(value, source);
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
		Optional<Holder.Reference<ConfiguredPower<?, ?>>> optionalInstance = powers.getHolder(power);
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
				for (ResourceKey<ConfiguredPower<?, ?>> value : instance.value().getContainedPowerKeys()) {
					Apoli.LOGGER.info("Adding subpower {} from power {}", value, power);
					this.addPower(value, source);
				}
				return true;
			}
		} else {
			for (ResourceKey<ConfiguredPower<?, ?>> value : instance.value().getContainedPowerKeys()) {
				Apoli.LOGGER.info("Adding subpower {} from power {}", value, power);
				this.addPower(value, source);
			}
			Set<ResourceLocation> sources = new HashSet<>();
			sources.add(source);
			this.powerSources.put(power, sources);
			this.powers.put(power, instance);
			this.factoryAccessCache.computeIfAbsent(instance.value().getFactory(), k -> Collections.synchronizedList(new ArrayList<>())).add(instance);
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
	public boolean hasPower(PowerFactory<?> factory) {
		List<Holder<ConfiguredPower<?, ?>>> access = this.factoryAccessCache.get(factory);
		if (access == null) return false;
		for (Holder<ConfiguredPower<?, ?>> holder : access) {
			if (holder.isBound() && holder.value().isActive(this.owner))
				return true;
		}
		return false;
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
		Set<ResourceKey<ConfiguredPower<?, ?>>> subPowers = this.powers.entrySet().stream().flatMap(x -> x.getValue().value().getChildrenKeys().stream()).collect(Collectors.toUnmodifiableSet());
		return this.powers.keySet().stream().filter(x -> !subPowers.contains(x)).collect(Collectors.toUnmodifiableSet());
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public @NotNull <C extends IDynamicFeatureConfiguration, F extends PowerFactory<C>> List<Holder<ConfiguredPower<C,F>>> getPowers(F factory, @NotNull Predicate<Holder<ConfiguredPower<C, F>>> filter) {
		List<Holder<ConfiguredPower<?, ?>>> access = this.factoryAccessCache.get(factory);
		if (access == null) return ImmutableList.of();
		List<Holder<ConfiguredPower<C, F>>> result = new ArrayList<>(access.size());
		for (Holder<ConfiguredPower<?, ?>> holder : access) {
			Holder<ConfiguredPower<C, F>> holderCast = (Holder) holder;
			if (holder.isBound() && filter.test(holderCast))
				result.add(holderCast);
		}
		return result;
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
		boolean removedAny = false;
		while (iterator.hasNext()) {
			Holder<ConfiguredPower<?, ?>> value = iterator.next();
			if (value.isBound())
				value.value().tick(this.owner);
			else {
				iterator.remove();
				removedAny = true;
			}
		}
		if (removedAny)
			this.rebuildFactoryAccessCache();
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
						Optional<Holder.Reference<ConfiguredPower<?, ?>>> optionalPower = powers.getHolder(identifier).filter(Holder::isBound);
						if (optionalPower.isEmpty()) {
							Apoli.LOGGER.warn("Power data of unregistered power \"" + identifier + "\" found on entity, skipping...");
							continue;
						}
						Holder<ConfiguredPower<?, ?>> instance = optionalPower.get();
						try {
							instance.value().deserialize(owner, this, data);
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
					for (ResourceKey<ConfiguredPower<?, ?>> subPower : power.getContainedPowerKeys()) {
						for (ResourceLocation source : entry.getValue()) {
							if (!this.hasPower(subPower, source))
								this.addPower(subPower, source);
						}
					}
				}
			}
			this.rebuildFactoryAccessCache();
		} catch (Exception e) {
			Apoli.LOGGER.info("Error while reading data: " + e.getMessage());
		}
	}

	private void rebuildFactoryAccessCache() {
		this.factoryAccessCache.clear();
		for (var value : this.powers.values()) {
			if (value.isBound()) {
				this.factoryAccessCache.computeIfAbsent(value.value().getFactory(), k -> Collections.synchronizedList(new ArrayList<>())).add(value);
			}
		}
	}

	@Override
	public void rebuildCache() { //Storing powers
		ImmutableSet<ResourceKey<ConfiguredPower<?, ?>>> powers = ImmutableSet.copyOf(this.powers.keySet());
		Registry<ConfiguredPower<?, ?>> registry = ApoliAPI.getPowers();
		for (ResourceKey<ConfiguredPower<?, ?>> power : powers) {
			Optional<Holder.Reference<ConfiguredPower<?, ?>>> holder = registry.getHolder(power).filter(Holder::isBound);
			if (holder.isPresent()) {
				this.powers.put(power, holder.get());
			} else {
				this.powerSources.get(power).forEach(source -> this.removePower(power, source)); //Safely remove powers while the previous is still in cache.
				Apoli.LOGGER.warn("Power {} was removed from entity {} as it doesn't exist anymore.", power, this.owner.getScoreboardName());
			}
		}
		this.rebuildFactoryAccessCache();
	}

	@Override
	public void handle(Multimap<ResourceLocation, ResourceLocation> powerSources, Map<ResourceLocation, CompoundTag> data) {
		this.powerSources.clear();
		this.powers.clear();
		this.powerData.clear();
		Registry<ConfiguredPower<?, ?>> powerRegistry = CalioAPI.getRegistryAccess().registryOrThrow(ApoliDynamicRegistries.CONFIGURED_POWER_KEY);
		for (Map.Entry<ResourceLocation, Collection<ResourceLocation>> powerEntry : powerSources.asMap().entrySet()) {
			ResourceKey<ConfiguredPower<?, ?>> power = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, powerEntry.getKey());
			Optional<Holder.Reference<ConfiguredPower<?, ?>>> configuredPower = powerRegistry.getHolder(power).filter(Holder::isBound);
			if (configuredPower.isEmpty()) {
				Apoli.LOGGER.warn("Received missing power {} from server for entity {}", power, this.owner.getScoreboardName());
				continue;
			}
			this.powers.put(power, configuredPower.get());
			this.powerSources.put(power, new HashSet<>(powerEntry.getValue()));
			CompoundTag tag = data.get(powerEntry.getKey());
			if (tag != null)
				configuredPower.get().value().deserialize(owner, this, tag);
		}
		this.rebuildFactoryAccessCache();
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
	public <T> @NotNull T getPowerData(ResourceKey<ConfiguredPower<?, ?>> power, Supplier<@NotNull T> supplier) {
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

	@Nullable
	public static PowerContainer get(Entity entity) {
		return ApoliCapabilities.POWER_CONTAINER.getCapability(entity, null);
	}

	@Override
	public Tag serializeNBT(HolderLookup.Provider provider) {
		return this.writeToNbt(new CompoundTag());
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, Tag nbt) {
		this.readFromNbt((CompoundTag) nbt);
        GlobalPowerSetUtil.applyGlobalPowers(owner);
	}
}
