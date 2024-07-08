package io.github.edwinmindcraft.apoli.api.configuration;

import com.google.common.collect.ImmutableMap;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.registry.CalioDynamicRegistryManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Used to avoid boilerplate code by implementing {@link IDynamicFeatureConfiguration#getWarnings(CalioDynamicRegistryManager)} and
 * {@link IDynamicFeatureConfiguration#getErrors(CalioDynamicRegistryManager)} for children.
 */
public interface IStreamConfiguration<T> extends IDynamicFeatureConfiguration {
	List<T> entries();

	@Override
	default @NotNull Map<String, IDynamicFeatureConfiguration> getChildrenComponent() {
		ImmutableMap.Builder<String, IDynamicFeatureConfiguration> components = ImmutableMap.builder();
		int i = 0;
		for (T entry : this.entries()) {
			if (entry instanceof IDynamicFeatureConfiguration config)
				components.put(Integer.toString(i), config);
			++i;
		}
		return components.build();
	}
}
