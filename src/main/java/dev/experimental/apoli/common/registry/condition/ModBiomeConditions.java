package dev.experimental.apoli.common.registry.condition;


import dev.experimental.apoli.api.MetaFactories;
import dev.experimental.apoli.api.power.configuration.ConfiguredBiomeCondition;
import dev.experimental.apoli.common.condition.biome.DelegatedBiomeCondition;
import dev.experimental.apoli.common.condition.biome.FloatComparingBiomeCondition;
import dev.experimental.apoli.common.condition.biome.HighHumidityCondition;
import dev.experimental.apoli.common.condition.biome.StringBiomeCondition;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.fmllegacy.RegistryObject;

import java.util.function.BiPredicate;

import static dev.experimental.apoli.common.registry.ApoliRegisters.BIOME_CONDITIONS;

public class ModBiomeConditions {
	public static final BiPredicate<ConfiguredBiomeCondition<?, ?>, Biome> PREDICATE = (config, biome) -> config.check(biome);

	public static final RegistryObject<StringBiomeCondition> CATEGORY = BIOME_CONDITIONS.register("category", () -> new StringBiomeCondition("category", biome -> biome.getBiomeCategory().getName()));
	public static final RegistryObject<HighHumidityCondition> HIGH_HUMIDITY = BIOME_CONDITIONS.register("high_humidity", HighHumidityCondition::new);
	public static final RegistryObject<StringBiomeCondition> PRECIPITATION = BIOME_CONDITIONS.register("precipitation", () -> new StringBiomeCondition("precipitation", biome -> biome.getBiomeCategory().getName()));
	public static final RegistryObject<FloatComparingBiomeCondition> TEMPERATURE = BIOME_CONDITIONS.register("temperature", () -> new FloatComparingBiomeCondition(Biome::getBaseTemperature));

	public static void register() {
		MetaFactories.defineMetaConditions(BIOME_CONDITIONS, DelegatedBiomeCondition::new, ConfiguredBiomeCondition.CODEC, PREDICATE);
	}
}
