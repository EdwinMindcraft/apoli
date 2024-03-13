package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.registry.ICalioDynamicRegistryManager;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record StatusEffectConfiguration(MobEffect effect,
										int minAmplifier, int maxAmplifier, int minDuration,
										int maxDuration) implements IDynamicFeatureConfiguration {

	public static final Codec<StatusEffectConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.STATUS_EFFECT.fieldOf("effect").forGetter(StatusEffectConfiguration::effect),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("min_amplifier", 0).forGetter(StatusEffectConfiguration::minAmplifier),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_amplifier", Integer.MAX_VALUE).forGetter(StatusEffectConfiguration::maxAmplifier),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("min_duration", 0).forGetter(StatusEffectConfiguration::minDuration),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_duration", Integer.MAX_VALUE).forGetter(StatusEffectConfiguration::maxDuration)
	).apply(instance, StatusEffectConfiguration::new));

	@Override
	public @NotNull List<String> getErrors(@NotNull ICalioDynamicRegistryManager server) {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		if (this.minAmplifier() <= this.maxAmplifier())
			builder.add("%s/Amplifier range is invalid: [%d,%d]".formatted(this.name(), this.minAmplifier(), this.maxAmplifier()));
		if (this.minDuration() <= this.maxDuration())
			builder.add("%s/Duration range is invalid: [%d,%d]".formatted(this.name(), this.minDuration(), this.maxDuration()));
		return builder.build();
	}

	@Override
	public @NotNull List<String> getWarnings(@NotNull ICalioDynamicRegistryManager server) {
		if (this.effect() == null)
			return ImmutableList.of(this.name() + "/Missing Effect");
		return ImmutableList.of();
	}

	@Override
	public boolean isConfigurationValid() {
		return this.effect() != null && this.minAmplifier() <= this.maxAmplifier() && this.minDuration() <= this.maxDuration();
	}
}
