package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.resources.ResourceLocation;

public record OverlayConfiguration(ResourceLocation texture, float strength, ColorConfiguration color, DrawMode mode,
								   DrawPhase phase, boolean hideWithHud,
								   boolean visibleInThirdPerson) implements IDynamicFeatureConfiguration {

	public static final Codec<OverlayConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.IDENTIFIER.fieldOf("texture").forGetter(OverlayConfiguration::texture),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.FLOAT, "strength", 1.0F).forGetter(OverlayConfiguration::strength),
			ColorConfiguration.NO_ALPHA.forGetter(OverlayConfiguration::color),
			DrawMode.CODEC.fieldOf("draw_mode").forGetter(OverlayConfiguration::mode),
			DrawPhase.CODEC.fieldOf("draw_phase").forGetter(OverlayConfiguration::phase),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "hide_with_hud", true).forGetter(OverlayConfiguration::hideWithHud),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "visible_in_third_person", false).forGetter(OverlayConfiguration::visibleInThirdPerson)
	).apply(instance, OverlayConfiguration::new));

	public enum DrawMode {
		NAUSEA,
		TEXTURE;

		public static final Codec<DrawMode> CODEC = SerializableDataType.enumValue(DrawMode.class);
	}

	public enum DrawPhase {
		BELOW_HUD,
		ABOVE_HUD;

		public static final Codec<DrawPhase> CODEC = SerializableDataType.enumValue(DrawPhase.class);
	}
}
