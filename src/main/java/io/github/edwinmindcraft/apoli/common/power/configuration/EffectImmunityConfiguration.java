package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.world.effect.MobEffect;

public record EffectImmunityConfiguration(ListConfiguration<MobEffect> effects, boolean inverted) implements IDynamicFeatureConfiguration {
	public static final Codec<EffectImmunityConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.mapCodec(SerializableDataTypes.STATUS_EFFECT, "effect", "effects").forGetter(EffectImmunityConfiguration::effects),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "inverted", false).forGetter(EffectImmunityConfiguration::inverted)
	).apply(instance, EffectImmunityConfiguration::new));
}
