package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.world.effect.MobEffectInstance;

public record SpawnEffectCloudConfiguration(float radius, float radiusOnUse, int waitTime,
											ListConfiguration<MobEffectInstance> effects) implements IDynamicFeatureConfiguration {

	public static final Codec<SpawnEffectCloudConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.FLOAT, "radius", 3.0F).forGetter(x -> x.radius),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.FLOAT, "radius_on_use", -0.5F).forGetter(x -> x.radiusOnUse),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "wait_time", 10).forGetter(x -> x.waitTime),
			ListConfiguration.mapCodec(SerializableDataTypes.STATUS_EFFECT_INSTANCE, "effect", "effects").forGetter(x -> x.effects)
	).apply(instance, SpawnEffectCloudConfiguration::new));
}
