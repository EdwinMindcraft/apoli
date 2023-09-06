package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiomeCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.BiomeConfiguration;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;

public class BiomeCondition extends EntityCondition<BiomeConfiguration> {

	public BiomeCondition() {
		super(BiomeConfiguration.CODEC);
	}

	@Override
	public boolean check(BiomeConfiguration configuration, Entity entity) {
		Holder<Biome> biome = entity.level().getBiome(entity.blockPosition());
		if (!ConfiguredBiomeCondition.check(configuration.condition(), biome))
			return false;
		if (configuration.biomes().getContent().isEmpty()) //No biome
			return true;
		return biome.unwrapKey().map(x -> configuration.biomes().getContent().stream().anyMatch(x::equals)).orElse(false);
	}
}
