package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.HudRender;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IActiveCooldownPowerConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public record ActiveSelfConfiguration(int duration, HudRender hudRender,
									  ConfiguredEntityAction<?, ?> action,
									  IActivePower.Key key) implements IActiveCooldownPowerConfiguration {
	public static final Codec<ActiveSelfConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "cooldown", 1).forGetter(ActiveSelfConfiguration::duration),
			ExtraCodecs.strictOptionalField(ApoliDataTypes.HUD_RENDER, "hud_render", HudRender.DONT_RENDER).forGetter(ActiveSelfConfiguration::hudRender),
			ConfiguredEntityAction.CODEC.fieldOf("entity_action").forGetter(ActiveSelfConfiguration::action),
			ExtraCodecs.strictOptionalField(IActivePower.Key.BACKWARD_COMPATIBLE_CODEC, "key", IActivePower.Key.PRIMARY).forGetter(ActiveSelfConfiguration::key)
	).apply(instance, ActiveSelfConfiguration::new));
}
