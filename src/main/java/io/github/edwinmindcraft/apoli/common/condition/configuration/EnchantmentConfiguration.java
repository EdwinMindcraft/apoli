package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.google.common.collect.Streams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.IntegerComparisonConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.registry.ICalioDynamicRegistryManager;
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
									   Optional<Enchantment> enchantment,
									   Calculation calculation) implements IDynamicFeatureConfiguration {
    public static final Codec<EnchantmentConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IntegerComparisonConfiguration.MAP_CODEC.forGetter(EnchantmentConfiguration::comparison),
            ExtraCodecs.strictOptionalField(SerializableDataTypes.ENCHANTMENT, "enchantment").forGetter(EnchantmentConfiguration::enchantment),
            SerializableDataType.enumValue(Calculation.class).optionalFieldOf("calculation", Calculation.SUM).forGetter(EnchantmentConfiguration::calculation)
    ).apply(instance, EnchantmentConfiguration::new));

	@Override
	public @NotNull List<String> getErrors(@NotNull ICalioDynamicRegistryManager server) {
		return IDynamicFeatureConfiguration.super.getErrors(server);
	}

	@Override
	public @NotNull List<String> getWarnings(@NotNull ICalioDynamicRegistryManager server) {
		return IDynamicFeatureConfiguration.super.getWarnings(server);
	}

	public boolean applyCheck(Iterable<ItemStack> input) {
		if (this.enchantment().isEmpty()) {
            return this.comparison().check(this.calculation().apply(Streams.stream(input).mapToInt(stack -> EnchantmentHelper.getEnchantments(stack).size())).orElse(0));
        }
		return this.comparison().check(this.calculation().apply(Streams.stream(input).mapToInt(stack -> EnchantmentHelper.getItemEnchantmentLevel(this.enchantment().get(), stack))).orElse(0));
	}

	public boolean applyCheck(ItemStack... stacks) {
		return this.applyCheck(Arrays.asList(stacks));
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
