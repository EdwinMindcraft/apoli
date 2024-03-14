package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public record InvisibilityConfiguration(boolean renderArmor, boolean renderOutline) implements IDynamicFeatureConfiguration {
    public static final Codec<InvisibilityConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "render_armor", false).forGetter(InvisibilityConfiguration::renderArmor),
            ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "render_outline", false).forGetter(InvisibilityConfiguration::renderOutline)
    ).apply(instance, InvisibilityConfiguration::new));
}
