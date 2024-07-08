package io.github.edwinmindcraft.apoli.api.power;

import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.registry.CalioDynamicRegistryManager;
import net.minecraft.core.RegistryAccess;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public abstract class ConfiguredFactory<T extends IDynamicFeatureConfiguration, F, CF extends ConfiguredFactory<?, ?, CF>> implements IDynamicFeatureConfiguration {
	private final Lazy<F> factory;
	private final T configuration;

	protected ConfiguredFactory(Supplier<F> factory, T configuration) {
		this.factory = Lazy.of(factory);
		this.configuration = configuration;
	}

	public F getFactory() {
		return this.factory.get();
	}

	public T getConfiguration() {
		return this.configuration;
	}

	@Override
	public @NotNull List<String> getErrors(@NotNull RegistryAccess server) {
		return this.getConfiguration().getErrors(server);
	}

	@Override
	public @NotNull List<String> getWarnings(@NotNull RegistryAccess server) {
		return this.getConfiguration().getWarnings(server);
	}

	@Override
	public boolean isConfigurationValid() {
		return this.getConfiguration().isConfigurationValid();
	}
}
