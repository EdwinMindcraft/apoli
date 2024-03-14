package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ElytraFlightConfiguration(boolean render,
										@Nullable ResourceLocation texture) implements IDynamicFeatureConfiguration {
	public static final Codec<ElytraFlightConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			CalioCodecHelper.BOOL.fieldOf("render_elytra").forGetter(ElytraFlightConfiguration::render),
			ExtraCodecs.strictOptionalField(SerializableDataTypes.IDENTIFIER, "texture_location").forGetter(x -> Optional.ofNullable(x.texture()))
	).apply(instance, (t1, t2) -> new ElytraFlightConfiguration(t1, t2.orElse(null))));
}
