package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.InteractionPowerConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record BiEntityInteractionConfiguration(Holder<ConfiguredBiEntityCondition<?, ?>> biEntityCondition,
											   Holder<ConfiguredBiEntityAction<?, ?>> biEntityAction,
											   int priority,
											   InteractionPowerConfiguration interaction) implements IDynamicFeatureConfiguration {

	public static Codec<BiEntityInteractionConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredBiEntityCondition.optional("bientity_condition").forGetter(BiEntityInteractionConfiguration::biEntityCondition),
			ConfiguredBiEntityAction.optional("bientity_action").forGetter(BiEntityInteractionConfiguration::biEntityAction),
			CalioCodecHelper.optionalField(CalioCodecHelper.INT, "priority", 0).forGetter(BiEntityInteractionConfiguration::priority),
			InteractionPowerConfiguration.MAP_CODEC.forGetter(BiEntityInteractionConfiguration::interaction)
	).apply(instance, BiEntityInteractionConfiguration::new));

	public static Codec<BiEntityInteractionConfiguration> PREVENTING_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredBiEntityCondition.optional("bientity_condition").forGetter(BiEntityInteractionConfiguration::biEntityCondition),
			ConfiguredBiEntityAction.optional("bientity_action").forGetter(BiEntityInteractionConfiguration::biEntityAction),
			InteractionPowerConfiguration.PREVENTING_MAP_CODEC.forGetter(BiEntityInteractionConfiguration::interaction)
	).apply(instance, BiEntityInteractionConfiguration::new));

	public BiEntityInteractionConfiguration(Holder<ConfiguredBiEntityCondition<?, ?>> biEntityCondition,
											Holder<ConfiguredBiEntityAction<?, ?>> biEntityAction,
											InteractionPowerConfiguration interaction) {
		this(biEntityCondition, biEntityAction, 0, interaction);
	}

	public boolean check(Entity actor, Entity target, InteractionHand hand, ItemStack held) {
		return this.interaction().appliesTo(actor.level(), hand, held) && ConfiguredBiEntityCondition.check(this.biEntityCondition(), actor, target);
	}

	public InteractionResult executeAction(Entity actor, Entity target, InteractionHand hand) {
		ConfiguredBiEntityAction.execute(this.biEntityAction(), actor, target);
		if (actor instanceof LivingEntity living)
			this.interaction().performActorItemStuff(living, hand);
		return this.interaction().actionResult();
	}
}
