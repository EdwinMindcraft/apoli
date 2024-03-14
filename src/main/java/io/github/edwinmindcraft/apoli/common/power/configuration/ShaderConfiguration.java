package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.resources.ResourceLocation;

public record ShaderConfiguration(ResourceLocation shader, boolean toggleable) implements IDynamicFeatureConfiguration {
	public static final Codec<ShaderConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.IDENTIFIER.fieldOf("shader").forGetter(ShaderConfiguration::shader),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "toggleable", true).forGetter(ShaderConfiguration::toggleable)
	).apply(instance, ShaderConfiguration::new));
}
