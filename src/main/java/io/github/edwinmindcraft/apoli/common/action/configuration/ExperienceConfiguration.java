package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public record ExperienceConfiguration(int points, int levels) implements IDynamicFeatureConfiguration {

	public static final Codec<ExperienceConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "points", 0).forGetter(ExperienceConfiguration::points),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "levels", 0).forGetter(ExperienceConfiguration::levels)
	).apply(instance, ExperienceConfiguration::new));
}
