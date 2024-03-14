package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;

public record EntityGlowConfiguration(Holder<ConfiguredEntityCondition<?, ?>> entityCondition,
									  Holder<ConfiguredBiEntityCondition<?, ?>> biEntityCondition, boolean useTeams,
									  ColorConfiguration color) implements IDynamicFeatureConfiguration {
	public EntityGlowConfiguration(Holder<ConfiguredEntityCondition<?, ?>> entityCondition) {
		this(entityCondition, null, true, ColorConfiguration.DEFAULT);
	}

	public static final Codec<EntityGlowConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredEntityCondition.optional("entity_condition").forGetter(EntityGlowConfiguration::entityCondition),
			ConfiguredBiEntityCondition.optional("bientity_condition").forGetter(EntityGlowConfiguration::biEntityCondition),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "use_teams", true).forGetter(EntityGlowConfiguration::useTeams),
			ColorConfiguration.NO_ALPHA.forGetter(EntityGlowConfiguration::color)
	).apply(instance, EntityGlowConfiguration::new));

	public boolean applyChecks(Entity actor, Entity target, boolean targetSelf) {
		return ConfiguredEntityCondition.check(this.entityCondition(), target) && ConfiguredBiEntityCondition.check(this.biEntityCondition(), targetSelf ? target : actor, targetSelf ? actor : target);
	}
}
