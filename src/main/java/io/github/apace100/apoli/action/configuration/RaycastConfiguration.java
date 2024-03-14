package io.github.apace100.apoli.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.configuration.RaycastSettingsConfiguration;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;

import javax.annotation.Nullable;
import java.util.Optional;

public record RaycastConfiguration(RaycastSettingsConfiguration settings,
								   Holder<ConfiguredEntityAction<?, ?>> beforeAction,
								   Holder<ConfiguredBiEntityCondition<?, ?>> biEntityCondition,
								   CommandInfo commandInfo, HitAction action) implements IDynamicFeatureConfiguration {
	public static final Codec<RaycastConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RaycastSettingsConfiguration.MAP_CODEC.forGetter(RaycastConfiguration::settings),
			ConfiguredEntityAction.optional("before_action").forGetter(RaycastConfiguration::beforeAction),
			ConfiguredBiEntityCondition.optional("bientity_condition").forGetter(RaycastConfiguration::biEntityCondition),
			CommandInfo.MAP_CODEC.forGetter(RaycastConfiguration::commandInfo),
			HitAction.MAP_CODEC.forGetter(RaycastConfiguration::action)
	).apply(instance, RaycastConfiguration::new));

	public record CommandInfo(@Nullable String commandAtHit, @Nullable Double commandHitOffset,
							  @Nullable String commandAlongRay,
							  double commandStep,
							  boolean commandAlongRayOnlyOnHit) implements IDynamicFeatureConfiguration {
		private static final MapCodec<CommandInfo> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				ExtraCodecs.strictOptionalField(Codec.STRING, "command_at_hit").forGetter(x -> Optional.ofNullable(x.commandAtHit())),
				ExtraCodecs.strictOptionalField(CalioCodecHelper.DOUBLE, "command_hit_offset").forGetter(x -> Optional.ofNullable(x.commandHitOffset())),
				ExtraCodecs.strictOptionalField(Codec.STRING, "command_along_ray").forGetter(x -> Optional.ofNullable(x.commandAlongRay())),
				ExtraCodecs.strictOptionalField(CalioCodecHelper.DOUBLE, "command_step", 1.0).forGetter(CommandInfo::commandStep),
				ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "command_along_ray_only_on_hit", false).forGetter(CommandInfo::commandAlongRayOnlyOnHit)
		).apply(instance, (t1, t2, t3, t4, t5) -> new CommandInfo(t1.orElse(null), t2.orElse(null), t3.orElse(null), t4, t5)));
	}

	public record HitAction(Holder<ConfiguredBlockAction<?, ?>> blockAction,
							Holder<ConfiguredEntityAction<?, ?>> hitAction,
							Holder<ConfiguredEntityAction<?, ?>> missAction,
							Holder<ConfiguredBiEntityAction<?, ?>> biEntityAction) implements IDynamicFeatureConfiguration {
		private static final MapCodec<HitAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				ConfiguredBlockAction.optional("block_action").forGetter(HitAction::blockAction),
				ConfiguredEntityAction.optional("hit_action").forGetter(HitAction::hitAction),
				ConfiguredEntityAction.optional("miss_action").forGetter(HitAction::missAction),
				ConfiguredBiEntityAction.optional("bientity_action").forGetter(HitAction::biEntityAction)
		).apply(instance, HitAction::new));
	}
}
