package io.github.apace100.apoli.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.MustBeBound;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;

public record AreaOfEffectConfiguration(double radius, @MustBeBound Holder<ConfiguredBiEntityAction<?, ?>> action,
										Holder<ConfiguredBiEntityCondition<?, ?>> condition,
										boolean includeTarget) implements IDynamicFeatureConfiguration {
	public static final Codec<AreaOfEffectConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			CalioCodecHelper.optionalField(CalioCodecHelper.DOUBLE, "radius", 16.0).forGetter(AreaOfEffectConfiguration::radius),
			ConfiguredBiEntityAction.required("bientity_action").forGetter(AreaOfEffectConfiguration::action),
			ConfiguredBiEntityCondition.optional("bientity_condition").forGetter(AreaOfEffectConfiguration::condition),
			CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "include_target", false).forGetter(AreaOfEffectConfiguration::includeTarget)
	).apply(instance, AreaOfEffectConfiguration::new));
}
