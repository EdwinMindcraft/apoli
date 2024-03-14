package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.HudRender;
import io.github.edwinmindcraft.apoli.api.configuration.MustBeBound;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredDamageCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.ICooldownPowerConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public record ConditionedCombatActionConfiguration(int duration, HudRender hudRender,
												   Holder<ConfiguredDamageCondition<?, ?>> damageCondition,
												   Holder<ConfiguredEntityCondition<?, ?>> targetCondition,
												   @MustBeBound Holder<ConfiguredEntityAction<?, ?>> entityAction) implements ICooldownPowerConfiguration {
	public static final Codec<ConditionedCombatActionConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "cooldown", 1).forGetter(ConditionedCombatActionConfiguration::duration),
			ExtraCodecs.strictOptionalField(ApoliDataTypes.HUD_RENDER, "hud_render", HudRender.DONT_RENDER).forGetter(ConditionedCombatActionConfiguration::hudRender),
			ConfiguredDamageCondition.optional("damage_condition").forGetter(ConditionedCombatActionConfiguration::damageCondition),
			ConfiguredEntityCondition.optional("target_condition").forGetter(ConditionedCombatActionConfiguration::targetCondition),
			ConfiguredEntityAction.required("entity_action").forGetter(ConditionedCombatActionConfiguration::entityAction)
	).apply(instance, ConditionedCombatActionConfiguration::new));

	public boolean check(Entity target, DamageSource source, float amount) {
		return ConfiguredEntityCondition.check(this.targetCondition(), target) && ConfiguredDamageCondition.check(this.damageCondition(), source, amount);
	}
}
