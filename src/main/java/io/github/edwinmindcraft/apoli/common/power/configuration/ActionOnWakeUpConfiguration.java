package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

public record ActionOnWakeUpConfiguration(Holder<ConfiguredBlockCondition<?, ?>> blockCondition,
										  Holder<ConfiguredEntityAction<?, ?>> entityAction,
										  Holder<ConfiguredBlockAction<?, ?>> blockAction) implements IDynamicFeatureConfiguration {

	public static final Codec<ActionOnWakeUpConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredBlockCondition.optional("block_condition").forGetter(ActionOnWakeUpConfiguration::blockCondition),
			ConfiguredEntityAction.optional("entity_action").forGetter(ActionOnWakeUpConfiguration::entityAction),
			ConfiguredBlockAction.optional("block_action").forGetter(ActionOnWakeUpConfiguration::blockAction)
	).apply(instance, ActionOnWakeUpConfiguration::new));

	public ActionOnWakeUpConfiguration(@Nullable ConfiguredBlockCondition<?, ?> blockCondition, @Nullable ConfiguredEntityAction<?, ?> entityAction, @Nullable ConfiguredBlockAction<?, ?> blockAction) {
		this(
				blockCondition == null ? ApoliDefaultConditions.BLOCK_DEFAULT.getHolder().orElseThrow() : Holder.direct(blockCondition),
				entityAction == null ? ApoliDefaultActions.ENTITY_DEFAULT.getHolder().orElseThrow() : Holder.direct(entityAction),
				blockAction == null ? ApoliDefaultActions.BLOCK_DEFAULT.getHolder().orElseThrow() : Holder.direct(blockAction)
		);
	}
}
