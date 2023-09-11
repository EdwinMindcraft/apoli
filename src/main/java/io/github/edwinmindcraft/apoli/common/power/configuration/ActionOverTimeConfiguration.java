package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;

public record ActionOverTimeConfiguration(Holder<ConfiguredEntityAction<?, ?>> entityAction,
										  Holder<ConfiguredEntityAction<?, ?>> risingAction,
										  Holder<ConfiguredEntityAction<?, ?>> fallingAction,
										  int interval) implements IDynamicFeatureConfiguration {
	public static final Codec<ActionOverTimeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredEntityAction.optional("entity_action").forGetter(ActionOverTimeConfiguration::entityAction),
			ConfiguredEntityAction.optional("rising_action").forGetter(ActionOverTimeConfiguration::risingAction),
			ConfiguredEntityAction.optional("falling_action").forGetter(ActionOverTimeConfiguration::fallingAction),
			CalioCodecHelper.optionalField(Codec.intRange(1, Integer.MAX_VALUE), "interval", 20).forGetter(ActionOverTimeConfiguration::interval)
	).apply(instance, ActionOverTimeConfiguration::new));
}
