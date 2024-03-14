package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.HudRender;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.ICooldownPowerConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public record CooldownConfiguration(int duration, HudRender hudRender) implements ICooldownPowerConfiguration {
	public static final Codec<CooldownConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "cooldown", 1).forGetter(CooldownConfiguration::duration),
			ExtraCodecs.strictOptionalField(ApoliDataTypes.HUD_RENDER, "hud_render", HudRender.DONT_RENDER).forGetter(CooldownConfiguration::hudRender)
	).apply(instance, CooldownConfiguration::new));
}
