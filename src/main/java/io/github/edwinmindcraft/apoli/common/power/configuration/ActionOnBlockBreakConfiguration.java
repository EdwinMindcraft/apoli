package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;

public record ActionOnBlockBreakConfiguration(Holder<ConfiguredBlockCondition<?, ?>> blockCondition,
											  Holder<ConfiguredEntityAction<?, ?>> entityAction,
											  Holder<ConfiguredBlockAction<?, ?>> blockAction,
											  boolean onlyWhenHarvested) implements IDynamicFeatureConfiguration {

	public static final Codec<ActionOnBlockBreakConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredBlockCondition.optional("block_condition").forGetter(ActionOnBlockBreakConfiguration::blockCondition),
			ConfiguredEntityAction.optional("entity_action").forGetter(ActionOnBlockBreakConfiguration::entityAction),
			ConfiguredBlockAction.optional("block_action").forGetter(ActionOnBlockBreakConfiguration::blockAction),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "only_when_harvested", true).forGetter(ActionOnBlockBreakConfiguration::onlyWhenHarvested)
	).apply(instance, ActionOnBlockBreakConfiguration::new));
}
