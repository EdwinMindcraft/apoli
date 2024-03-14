package io.github.apace100.apoli.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public record ElytraFlightPossibleConfiguration(boolean checkState,
												boolean checkAbility) implements IDynamicFeatureConfiguration {
	public static final Codec<ElytraFlightPossibleConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "check_state", false).forGetter(ElytraFlightPossibleConfiguration::checkState),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "check_ability", true).forGetter(ElytraFlightPossibleConfiguration::checkAbility)
	).apply(instance, ElytraFlightPossibleConfiguration::new));
}
