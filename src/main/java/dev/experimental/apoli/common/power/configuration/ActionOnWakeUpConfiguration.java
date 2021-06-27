package dev.experimental.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.experimental.apoli.api.IDynamicFeatureConfiguration;
import dev.experimental.apoli.api.power.configuration.ConfiguredBlockAction;
import dev.experimental.apoli.api.power.configuration.ConfiguredBlockCondition;
import dev.experimental.apoli.api.power.configuration.ConfiguredEntityAction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ActionOnWakeUpConfiguration(@Nullable ConfiguredBlockCondition<?, ?> blockCondition,
										  @Nullable ConfiguredEntityAction<?, ?> entityAction,
										  @Nullable ConfiguredBlockAction<?, ?> blockAction) implements IDynamicFeatureConfiguration {

	public static final Codec<ActionOnWakeUpConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredBlockCondition.CODEC.optionalFieldOf("block_condition").forGetter(x -> Optional.ofNullable(x.blockCondition())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("entity_action").forGetter(x -> Optional.ofNullable(x.entityAction())),
			ConfiguredBlockAction.CODEC.optionalFieldOf("block_action").forGetter(x -> Optional.ofNullable(x.blockAction()))
	).apply(instance, (bc, ea, ba) -> new ActionOnWakeUpConfiguration(bc.orElse(null), ea.orElse(null), ba.orElse(null))));
}
