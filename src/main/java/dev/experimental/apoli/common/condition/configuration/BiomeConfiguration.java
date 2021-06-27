package dev.experimental.apoli.common.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.experimental.apoli.api.IDynamicFeatureConfiguration;
import dev.experimental.apoli.api.configuration.ListConfiguration;
import dev.experimental.apoli.api.power.configuration.ConfiguredBiomeCondition;
import dev.experimental.calio.api.network.CalioCodecHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record BiomeConfiguration(ListConfiguration<RegistryKey<Biome>> biomes,
								 @Nullable ConfiguredBiomeCondition<?, ?> condition) implements IDynamicFeatureConfiguration {

	public static final Codec<BiomeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.mapCodec(CalioCodecHelper.resourceKey(Registry.BIOME_KEY), "biome", "biomes").forGetter(BiomeConfiguration::biomes),
			ConfiguredBiomeCondition.CODEC.optionalFieldOf("condition").forGetter(x -> Optional.ofNullable(x.condition()))
	).apply(instance, (t1, t2) -> new BiomeConfiguration(t1, t2.orElse(null))));
}
