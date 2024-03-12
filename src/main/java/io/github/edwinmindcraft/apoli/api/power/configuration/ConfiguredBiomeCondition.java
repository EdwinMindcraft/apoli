package io.github.edwinmindcraft.apoli.api.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.ConfiguredCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.BiomeCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Supplier;

public final class ConfiguredBiomeCondition<C extends IDynamicFeatureConfiguration, F extends BiomeCondition<C>> extends ConfiguredCondition<C, F, ConfiguredBiomeCondition<?, ?>> {
	public static final Codec<ConfiguredBiomeCondition<?, ?>> CODEC = BiomeCondition.CODEC.dispatch(ConfiguredBiomeCondition::getFactory, BiomeCondition::getConditionCodec);
	public static final CodecSet<ConfiguredBiomeCondition<?, ?>> CODEC_SET = CalioCodecHelper.forDynamicRegistry(ApoliDynamicRegistries.CONFIGURED_BIOME_CONDITION_KEY, SerializableDataTypes.IDENTIFIER, CODEC);
	public static final Codec<Holder<ConfiguredBiomeCondition<?, ?>>> HOLDER = CODEC_SET.holder();

	public static MapCodec<Holder<ConfiguredBiomeCondition<?, ?>>> required(String name) {
		return HOLDER.fieldOf(name);
	}

	public static MapCodec<Holder<ConfiguredBiomeCondition<?, ?>>> optional(String name) {
		return CalioCodecHelper.registryDefaultedField(HOLDER, name, ApoliDynamicRegistries.CONFIGURED_BIOME_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_BIOME_CONDITIONS);
	}

	public static boolean check(Holder<ConfiguredBiomeCondition<?, ?>> condition, Holder<Biome> biome) {
		return !condition.isBound() || condition.value().check(biome);
	}

	public ConfiguredBiomeCondition(Supplier<F> factory, C configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(Holder<Biome> biome) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), biome);
	}

	@Override
	public String toString() {
		return "CBiC:" + ApoliRegistries.BIOME_CONDITION.getKey(this.getFactory()) + "(" + this.getData() + ")-" + this.getConfiguration();
	}
}