package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.world.effect.MobEffectInstance;

public record StackingStatusEffectConfiguration(ListConfiguration<MobEffectInstance> effects,
												int min, int max,
												int duration, int tickRate) implements IDynamicFeatureConfiguration {
	public static final Codec<StackingStatusEffectConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.mapCodec(SerializableDataTypes.STATUS_EFFECT_INSTANCE, "effect", "effects").forGetter(StackingStatusEffectConfiguration::effects),
			CalioCodecHelper.INT.fieldOf("min_stacks").forGetter(StackingStatusEffectConfiguration::min),
			CalioCodecHelper.INT.fieldOf("max_stacks").forGetter(StackingStatusEffectConfiguration::max),
			CalioCodecHelper.INT.fieldOf("duration_per_stack").forGetter(StackingStatusEffectConfiguration::duration),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "tick_rate", 10).forGetter(StackingStatusEffectConfiguration::tickRate)
	).apply(instance, StackingStatusEffectConfiguration::new));

	public StackingStatusEffectConfiguration(ListConfiguration<MobEffectInstance> effects, int min, int max, int duration) {
		this(effects, min, max, duration, 10);
	}
}
