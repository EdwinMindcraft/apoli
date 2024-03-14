package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.util.HudRender;
import io.github.edwinmindcraft.apoli.api.configuration.MustBeBound;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredDamageCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.ICooldownPowerConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;

public record CombatHitActionConfiguration(int duration, HudRender hudRender,
										   Holder<ConfiguredDamageCondition<?, ?>> damageCondition,
										   Holder<ConfiguredBiEntityCondition<?, ?>> biEntityCondition,
										   @MustBeBound Holder<ConfiguredBiEntityAction<?, ?>> biEntityAction) implements ICooldownPowerConfiguration {
	public static final Codec<CombatHitActionConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "cooldown", 1).forGetter(CombatHitActionConfiguration::duration),
			ExtraCodecs.strictOptionalField(HudRender.CODEC, "hud_render", HudRender.DONT_RENDER).forGetter(CombatHitActionConfiguration::hudRender),
			ConfiguredDamageCondition.optional("damage_condition").forGetter(CombatHitActionConfiguration::damageCondition),
			ConfiguredBiEntityCondition.optional("bientity_condition").forGetter(CombatHitActionConfiguration::biEntityCondition),
			ConfiguredBiEntityAction.required("bientity_action").forGetter(CombatHitActionConfiguration::biEntityAction)
	).apply(instance, CombatHitActionConfiguration::new));
}
