package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.PowerReference;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.OptionalFuncs;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Optional;

public record PowerSourceConfiguration(PowerReference power,
									   @UnknownNullability ResourceLocation source) implements IDynamicFeatureConfiguration {
	public static final Codec<PowerSourceConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PowerReference.mapCodec("power").forGetter(PowerSourceConfiguration::power),
			SerializableDataTypes.IDENTIFIER.fieldOf("source").forGetter(PowerSourceConfiguration::source)
	).apply(instance, PowerSourceConfiguration::new));

	public static final Codec<PowerSourceConfiguration> OPTIONAL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PowerReference.mapCodec("power").forGetter(PowerSourceConfiguration::power),
			ExtraCodecs.strictOptionalField(SerializableDataTypes.IDENTIFIER, "source").forGetter(OptionalFuncs.opt(PowerSourceConfiguration::source))
	).apply(instance, (PowerReference power1, Optional<ResourceLocation> source1) -> new PowerSourceConfiguration(power1, source1.orElse(null))));
}
