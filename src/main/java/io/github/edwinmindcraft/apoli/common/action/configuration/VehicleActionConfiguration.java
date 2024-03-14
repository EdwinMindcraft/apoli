package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;

public record VehicleActionConfiguration(Holder<ConfiguredEntityAction<?, ?>> action,
										 Holder<ConfiguredBiEntityAction<?, ?>> biEntityAction,
										 Holder<ConfiguredBiEntityCondition<?, ?>> biEntityCondition,
										 boolean recursive) implements IDynamicFeatureConfiguration {
	public static final Codec<VehicleActionConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredEntityAction.optional("action").forGetter(VehicleActionConfiguration::action),
			ConfiguredBiEntityAction.optional("bientity_action").forGetter(VehicleActionConfiguration::biEntityAction),
			ConfiguredBiEntityCondition.optional("bientity_condition").forGetter(VehicleActionConfiguration::biEntityCondition),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "recursive", false).forGetter(VehicleActionConfiguration::recursive)
	).apply(instance, VehicleActionConfiguration::new));
}
