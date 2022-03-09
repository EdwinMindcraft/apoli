package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public record DamageItemConfiguration(int amount, boolean ignoreUnbreaking) implements IDynamicFeatureConfiguration {
	public static final Codec<DamageItemConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			CalioCodecHelper.optionalField(Codec.INT, "amount", 1).forGetter(DamageItemConfiguration::amount),
			CalioCodecHelper.optionalField(Codec.BOOL, "ignore_unbreaking", false).forGetter(DamageItemConfiguration::ignoreUnbreaking)
	).apply(instance, DamageItemConfiguration::new));
}
