package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.google.common.collect.Streams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.IntegerComparisonConfiguration;
import io.github.edwinmindcraft.calio.api.registry.CalioDynamicRegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.IntStream;

public record EnchantmentConfiguration(IntegerComparisonConfiguration comparison,
									   Optional<ResourceKey<Enchantment>> enchantment,
									   Calculation calculation) implements IDynamicFeatureConfiguration {
    public static final MapCodec<EnchantmentConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IntegerComparisonConfiguration.CODEC.forGetter(EnchantmentConfiguration::comparison),
			SerializableDataTypes.ENCHANTMENT.optionalFieldOf("enchantment").forGetter(EnchantmentConfiguration::enchantment),
            SerializableDataType.enumValue(Calculation.class).optionalFieldOf("calculation", Calculation.SUM).forGetter(EnchantmentConfiguration::calculation)
    ).apply(instance, EnchantmentConfiguration::new));

	public boolean applyCheck(RegistryAccess access, Iterable<ItemStack> input) {
		if (this.enchantment().isEmpty()) {
            return this.comparison().check(this.calculation().apply(Streams.stream(input).mapToInt(stack -> EnchantmentHelper.getEnchantmentsForCrafting(stack).size())).orElse(0));
        }
		Holder<Enchantment> enchantment = access.holderOrThrow(enchantment().get());
		return this.comparison().check(this.calculation().apply(Streams.stream(input).mapToInt(stack -> stack.getEnchantmentLevel(enchantment))).orElse(0));
	}

	public boolean applyCheck(RegistryAccess access, ItemStack... stacks) {
		return this.applyCheck(access, Arrays.asList(stacks));
	}

	public enum Calculation {
		SUM(x -> x.reduce(Integer::sum)),
		MAX(IntStream::max);

		private final Function<IntStream, OptionalInt> collapse;

		Calculation(Function<IntStream, OptionalInt> collapse) {
			this.collapse = collapse;
		}

		public OptionalInt apply(IntStream stream) {
			return this.collapse.apply(stream);
		}
	}
}
