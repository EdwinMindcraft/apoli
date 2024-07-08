package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import net.minecraft.core.Holder;

public record ActionOnCallbackConfiguration(Holder<ConfiguredEntityAction<?, ?>> entityActionRespawned,
											Holder<ConfiguredEntityAction<?, ?>> entityActionRemoved,
											Holder<ConfiguredEntityAction<?, ?>> entityActionGained,
											Holder<ConfiguredEntityAction<?, ?>> entityActionLost,
											Holder<ConfiguredEntityAction<?, ?>> entityActionAdded) implements IDynamicFeatureConfiguration {
	public static final MapCodec<ActionOnCallbackConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ConfiguredEntityAction.optional("entity_action_respawned").forGetter(ActionOnCallbackConfiguration::entityActionRespawned),
			ConfiguredEntityAction.optional("entity_action_removed").forGetter(ActionOnCallbackConfiguration::entityActionRemoved),
			ConfiguredEntityAction.optional("entity_action_gained").forGetter(ActionOnCallbackConfiguration::entityActionGained),
			ConfiguredEntityAction.optional("entity_action_lost").forGetter(ActionOnCallbackConfiguration::entityActionLost),
			ConfiguredEntityAction.optional("entity_action_added").forGetter(ActionOnCallbackConfiguration::entityActionAdded)
	).apply(instance, ActionOnCallbackConfiguration::new));
}
