package io.github.edwinmindcraft.apoli.api.component;

import io.github.edwinmindcraft.apoli.api.power.PowerSource;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

public interface PowerContainer2 {
	/**
	 * Adds the given power to the current container.
	 *
	 * @param power  The power to be added.
	 * @param source The source of the power.
	 * @return If the source for the power was added to the container, returns {@code true}, otherwise {@code false}.
	 */
	boolean add(Holder<ConfiguredPower<?, ?>> power, PowerSource source);

	/**
	 * Checks if the current container has the given power.
	 *
	 * @param power           The power to check the existence of.
	 * @param includeInactive If {@code true}, this method will only return true if the power is active.
	 * @return If the power exists and {@code includeInactive} is false or the power is active, returns {@code true},
	 * otherwise {@code false}
	 */
	boolean contains(Holder<ConfiguredPower<?, ?>> power, boolean includeInactive);

	/**
	 * Removes the given source for the given power from the current container.
	 *
	 * @param power  The power to be removed.
	 * @param source The source of the power.
	 * @return If the source for this power existed and was removed, returns {@code true}, otherwise {@code false}.
	 */
	boolean remove(Holder<ConfiguredPower<?, ?>> power, PowerSource source);

	/**
	 * Removes the power and all of it's associated sources.
	 *
	 * @param power The power to be removed.
	 * @return If the power existed and was removed, returns {@code true}, otherwise {@code false}.
	 */
	boolean remove(Holder<ConfiguredPower<?, ?>> power);
}
