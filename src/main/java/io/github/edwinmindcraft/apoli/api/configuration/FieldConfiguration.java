package io.github.edwinmindcraft.apoli.api.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.registry.ICalioDynamicRegistryManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record FieldConfiguration<T>(T value) implements IDynamicFeatureConfiguration {
	public static <T> FieldConfiguration<T> of(T value) {
		return new FieldConfiguration<>(value);
	}

	public static <T> Codec<FieldConfiguration<T>> codec(Codec<T> codec, String fieldName, T defaultValue) {
		return CalioCodecHelper.optionalField(codec, fieldName, defaultValue).xmap(FieldConfiguration::new, FieldConfiguration::value).codec();
	}

	public static <T> Codec<FieldConfiguration<T>> codec(MapCodec<T> codec) {
		return codec.xmap(FieldConfiguration::new, FieldConfiguration::value).codec();
	}

	public static <T> Codec<FieldConfiguration<T>> codec(Codec<T> codec, String fieldName) {
		return codec.fieldOf(fieldName).xmap(FieldConfiguration::new, FieldConfiguration::value).codec();
	}

	public static <T> Codec<FieldConfiguration<Optional<T>>> optionalCodec(Codec<T> codec, String fieldName) {
		return CalioCodecHelper.optionalField(codec, fieldName).xmap(FieldConfiguration::new, FieldConfiguration::value).codec();
	}

	@Override
	public @NotNull List<String> getErrors(@NotNull ICalioDynamicRegistryManager server) {
		if (this.value() instanceof IDynamicFeatureConfiguration config)
			return config.copyErrorsFrom(config, server, this.name());
		return ImmutableList.of();
	}

	@Override
	public @NotNull List<String> getWarnings(@NotNull ICalioDynamicRegistryManager server) {
		if (this.value() instanceof IDynamicFeatureConfiguration config)
			return config.copyWarningsFrom(config, server, this.name());
		return ImmutableList.of();
	}

	@Override
	public boolean isConfigurationValid() {
		return !(this.value() instanceof IDynamicFeatureConfiguration config) || config.isConfigurationValid();
	}
}
