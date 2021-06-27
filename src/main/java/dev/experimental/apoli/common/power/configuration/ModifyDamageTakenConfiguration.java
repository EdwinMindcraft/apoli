package dev.experimental.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.experimental.apoli.api.configuration.ListConfiguration;
import dev.experimental.apoli.api.power.configuration.ConfiguredDamageCondition;
import dev.experimental.apoli.api.power.configuration.ConfiguredEntityAction;
import dev.experimental.apoli.api.power.configuration.power.IValueModifyingPowerConfiguration;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ModifyDamageTakenConfiguration(ListConfiguration<EntityAttributeModifier> modifiers,
											 @Nullable ConfiguredDamageCondition<?, ?> damageCondition,
											 @Nullable ConfiguredEntityAction<?, ?> selfAction,
											 @Nullable ConfiguredEntityAction<?, ?> targetAction) implements IValueModifyingPowerConfiguration {
	public static final Codec<ModifyDamageTakenConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.MODIFIER_CODEC.forGetter(ModifyDamageTakenConfiguration::modifiers),
			ConfiguredDamageCondition.CODEC.optionalFieldOf("damage_condition").forGetter(x -> Optional.ofNullable(x.damageCondition())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("self_action").forGetter(x -> Optional.ofNullable(x.selfAction())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("attacker_action").forGetter(x -> Optional.ofNullable(x.targetAction()))
	).apply(instance, (t1, t2, t3, t4) -> new ModifyDamageTakenConfiguration(t1, t2.orElse(null), t3.orElse(null), t4.orElse(null))));
}
