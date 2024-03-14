package io.github.edwinmindcraft.apoli.api.power;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.*;

/**
 * A wrapper class around a resource location, which, just like {@link TagKey} interns values whenever possible,
 * allowing us to rely on identity comparison to succeed for performance.
 */
public final class PowerSource {
	public static final Codec<PowerSource> CODEC = ResourceLocation.CODEC.xmap(PowerSource::create, PowerSource::location);

	private static final Interner<PowerSource> POWER_SOURCE_CACHE = Interners.newWeakInterner();

	/**
	 * Creates a power source, or finds an existing one with the given key.
	 *
	 * @param source The location of the power source
	 * @return The power source that existed previously, or a newly create one if none existed.
	 */
	public static PowerSource create(ResourceLocation source) {
		return POWER_SOURCE_CACHE.intern(new PowerSource(source));
	}

	private final ResourceLocation location;

	private PowerSource(ResourceLocation location) {
		this.location = location;
	}

	public ResourceLocation location() {
		return location;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (PowerSource) obj;
		return Objects.equals(this.location, that.location);
	}

	@Override
	public int hashCode() {
		return Objects.hash(location);
	}

	@Override
	public String toString() {
		return "PowerSource[" +
				"location=" + location + ']';
	}

}