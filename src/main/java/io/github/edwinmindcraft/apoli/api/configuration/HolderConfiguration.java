package io.github.edwinmindcraft.apoli.api.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.CalioAPI;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record HolderConfiguration<T>(Holder<T> holder, boolean required) implements IDynamicFeatureConfiguration {

	public static <T> HolderConfiguration<T> of(Holder<T> holder) {
		return new HolderConfiguration<>(holder, false);
	}

	public static <T> HolderConfiguration<T> defaulted(ResourceKey<Registry<T>> registryKey) {
		Registry<T> registry = CalioAPI.getRegistryAccess().registryOrThrow(registryKey);
		if (registry instanceof DefaultedMappedRegistry<T> defaulted)
			return new HolderConfiguration<>(registry.getHolder(ResourceKey.create(registryKey, defaulted.getDefaultKey())).orElseThrow(), false);
		throw new UnsupportedOperationException("Could not find default from registry '" + registryKey.location() + "'.");
	}

	public static <T> MapCodec<HolderConfiguration<T>> required(MapCodec<Holder<T>> codec) {
		return codec.xmap(x -> new HolderConfiguration<>(x, true), HolderConfiguration::holder);
	}

	public static <T> MapCodec<HolderConfiguration<T>> optional(MapCodec<Holder<T>> codec) {
		return codec.xmap(x -> new HolderConfiguration<>(x, false), HolderConfiguration::holder);
	}


	@Override
	public @NotNull List<String> getUnbound() {
		if (this.required() && !this.holder().isBound())
			return ImmutableList.of(IDynamicFeatureConfiguration.holderAsString("holder", this.holder()));
		return ImmutableList.of();
	}
}
