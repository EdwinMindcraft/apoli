package io.github.edwinmindcraft.apoli.api.power.factory;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.IConditionFactory;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiomeCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public abstract class BiomeCondition<T extends IDynamicFeatureConfiguration> implements IConditionFactory<T, ConfiguredBiomeCondition<T, ?>, BiomeCondition<T>> {
	public static final Codec<BiomeCondition<?>> CODEC = ApoliRegistries.BIOME_CONDITION.byNameCodec();

	private final Codec<ConfiguredBiomeCondition<T, ?>> codec;

	protected BiomeCondition(Codec<T> codec) {
		this.codec = IConditionFactory.conditionCodec(codec, this);
	}

	@Override
	public Codec<ConfiguredBiomeCondition<T, ?>> getConditionCodec() {
		return this.codec;
	}

	@Override
	public ConfiguredBiomeCondition<T, ?> configure(T input, ConditionData configuration) {
		return new ConfiguredBiomeCondition<>(() -> this, input, configuration);
	}

	protected boolean check(T configuration, Holder<Biome> biome) {
		return false;
	}

	public boolean check(T configuration, ConditionData data, Holder<Biome> biome) {
		return data.inverted() ^ this.check(configuration, biome);
	}
}
