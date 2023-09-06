package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiomeCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public record BiomeConfiguration(ListConfiguration<ResourceKey<Biome>> biomes,
								 Holder<ConfiguredBiomeCondition<?, ?>> condition) implements IDynamicFeatureConfiguration {

	public static final Codec<BiomeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.mapCodec(CalioCodecHelper.resourceKey(Registries.BIOME), "biome", "biomes").forGetter(BiomeConfiguration::biomes),
			ConfiguredBiomeCondition.optional("condition").forGetter(BiomeConfiguration::condition)
	).apply(instance, BiomeConfiguration::new));
}
