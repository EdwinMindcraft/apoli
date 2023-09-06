package io.github.edwinmindcraft.apoli.common.condition.biome;

import com.mojang.serialization.Codec;
import io.github.apace100.apoli.access.BiomeWeatherAccess;
import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BiomeCondition;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public class HighHumidityCondition extends BiomeCondition<NoConfiguration> {

	public static final Codec<HighHumidityCondition> CODEC = Codec.unit(new HighHumidityCondition());

	public HighHumidityCondition() {
		super(NoConfiguration.CODEC);
	}

	@Override
	protected boolean check(NoConfiguration configuration, Holder<Biome> biome) {
		return biome.isBound() && ((BiomeWeatherAccess)(Object)biome.value()).getDownfall() > 0.85F;
	}
}
