package io.github.edwinmindcraft.apoli.common.registry.condition;


import io.github.apace100.apoli.Apoli;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.MetaFactories;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiomeCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.BiomeCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.apoli.common.condition.biome.*;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConditionStreamConfiguration;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConstantConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.function.BiPredicate;

import static io.github.edwinmindcraft.apoli.common.registry.ApoliRegisters.BIOME_CONDITIONS;

public class ApoliBiomeConditions {
	public static final BiPredicate<ConfiguredBiomeCondition<?, ?>, Holder<Biome>> PREDICATE = ConfiguredBiomeCondition::check;

	private static <U extends BiomeCondition<?>> RegistryObject<U> of(String name) {
		return RegistryObject.create(Apoli.identifier(name), ApoliRegistries.BIOME_CONDITION_KEY.location(), Apoli.MODID);
	}

	public static final RegistryObject<DelegatedBiomeCondition<ConstantConfiguration<Holder<Biome>>>> CONSTANT = of("constant");
	public static final RegistryObject<DelegatedBiomeCondition<ConditionStreamConfiguration<ConfiguredBiomeCondition<?, ?>, Holder<Biome>>>> AND = of("and");
	public static final RegistryObject<DelegatedBiomeCondition<ConditionStreamConfiguration<ConfiguredBiomeCondition<?, ?>, Holder<Biome>>>> OR = of("or");

	public static final RegistryObject<CategoryBiomeCondition> CATEGORY = BIOME_CONDITIONS.register("category", CategoryBiomeCondition::new);
	public static final RegistryObject<HighHumidityCondition> HIGH_HUMIDITY = BIOME_CONDITIONS.register("high_humidity", HighHumidityCondition::new);
	public static final RegistryObject<PropertyBiomeCondition<Biome.Precipitation>> PRECIPITATION = BIOME_CONDITIONS.register("precipitation", () -> new PropertyBiomeCondition<>("precipitation", SerializableDataType.enumValue(Biome.Precipitation.class), (x) -> x.isBound() ? x.value().getPrecipitationAt(new BlockPos(0, 64, 0)) : null));
	public static final RegistryObject<FloatComparingBiomeCondition> TEMPERATURE = BIOME_CONDITIONS.register("temperature", () -> new FloatComparingBiomeCondition(x -> x.isBound() ? x.value().getBaseTemperature() : 0.2F));
	public static final RegistryObject<InTagCondition> IN_TAG = BIOME_CONDITIONS.register("in_tag", InTagCondition::new);

	public static ConfiguredBiomeCondition<?, ?> constant(boolean value) {return CONSTANT.get().configure(new ConstantConfiguration<>(value));}

	@SafeVarargs
	public static ConfiguredBiomeCondition<?, ?> and(HolderSet<ConfiguredBiomeCondition<?, ?>>... conditions) {return AND.get().configure(ConditionStreamConfiguration.and(Arrays.asList(conditions), PREDICATE));}

	@SafeVarargs
	public static ConfiguredBiomeCondition<?, ?> or(HolderSet<ConfiguredBiomeCondition<?, ?>>... conditions) {return OR.get().configure(ConditionStreamConfiguration.or(Arrays.asList(conditions), PREDICATE));}

	public static void bootstrap() {
		MetaFactories.defineMetaConditions(BIOME_CONDITIONS, DelegatedBiomeCondition::new, ConfiguredBiomeCondition.CODEC_SET, PREDICATE);
	}
}
