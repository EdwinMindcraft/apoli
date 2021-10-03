package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredDamageCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IValueModifyingPowerConfiguration;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ModifyDamageDealtConfiguration(ListConfiguration<AttributeModifier> modifiers,
											 @Nullable ConfiguredDamageCondition<?, ?> damageCondition,
											 @Nullable ConfiguredEntityCondition<?, ?> targetCondition,
											 @Nullable ConfiguredEntityAction<?, ?> selfAction,
											 @Nullable ConfiguredEntityAction<?, ?> targetAction) implements IValueModifyingPowerConfiguration {

	public ModifyDamageDealtConfiguration(AttributeModifier... modifiers) {
		this(ListConfiguration.of(modifiers), null, null, null, null);
	}

	public static final Codec<ModifyDamageDealtConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.MODIFIER_CODEC.forGetter(ModifyDamageDealtConfiguration::modifiers),
			ConfiguredDamageCondition.CODEC.optionalFieldOf("damage_condition").forGetter(x -> Optional.ofNullable(x.damageCondition())),
			ConfiguredEntityCondition.CODEC.optionalFieldOf("target_condition").forGetter(x -> Optional.ofNullable(x.targetCondition())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("self_action").forGetter(x -> Optional.ofNullable(x.selfAction())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("target_action").forGetter(x -> Optional.ofNullable(x.targetAction()))
	).apply(instance, (t1, t2, t3, t4, t5) -> new ModifyDamageDealtConfiguration(t1, t2.orElse(null), t3.orElse(null), t4.orElse(null), t5.orElse(null))));
}
