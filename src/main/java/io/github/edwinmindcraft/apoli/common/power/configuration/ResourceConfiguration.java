package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.HudRender;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IHudRenderedVariableIntPowerConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IVariableIntPowerConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;

import java.util.Optional;

public record ResourceConfiguration(HudRender hudRender, int initialValue, int min, int max,
									Holder<ConfiguredEntityAction<?, ?>> minAction,
									Holder<ConfiguredEntityAction<?, ?>> maxAction) implements IHudRenderedVariableIntPowerConfiguration {
	public static final Codec<ResourceConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(ApoliDataTypes.HUD_RENDER, "hud_render", HudRender.DONT_RENDER).forGetter(IHudRenderedVariableIntPowerConfiguration::hudRender),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "start_value").forGetter(x -> x.min() == x.initialValue() ? Optional.empty() : Optional.of(x.initialValue())),
			CalioCodecHelper.INT.fieldOf("min").forGetter(IVariableIntPowerConfiguration::min),
			CalioCodecHelper.INT.fieldOf("max").forGetter(IVariableIntPowerConfiguration::max),
			ConfiguredEntityAction.optional("min_action").forGetter(ResourceConfiguration::minAction),
			ConfiguredEntityAction.optional("max_action").forGetter(ResourceConfiguration::maxAction)
	).apply(instance, (t1, t2, t3, t4, t5, t6) -> new ResourceConfiguration(t1, t2.orElse(t3), t3, t4, t5, t6)));
}
