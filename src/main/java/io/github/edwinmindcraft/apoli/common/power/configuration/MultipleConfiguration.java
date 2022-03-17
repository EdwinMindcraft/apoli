package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.*;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import net.minecraft.util.Tuple;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

//Validation occurs on every subpower, this should be fine
public record MultipleConfiguration<V>(Map<String, V> children) implements IDynamicFeatureConfiguration {


	public static <V> MapCodec<MultipleConfiguration<V>> mapCodec(Codec<V> codec, Predicate<String> filter, UnaryOperator<String> keyMapper, Function3<String, V, JsonElement, V> configurator) {
		return new MultipleMapCodec<>(codec, filter, keyMapper, configurator);
	}

	private static final class MultipleMapCodec<V> extends MapCodec<MultipleConfiguration<V>> {
		private final Codec<V> codec;
		private final Predicate<String> keyFilter;
		private final UnaryOperator<String> keyMapper;
		private final Function3<String, V, JsonElement, V> configurator;

		private MultipleMapCodec(Codec<V> codec, Predicate<String> keyFilter, UnaryOperator<String> keyMapper, Function3<String, V, JsonElement, V> configurator) {
			this.codec = codec;
			this.keyFilter = keyFilter;
			this.keyMapper = keyMapper;
			this.configurator = configurator;
		}

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return ops.compressMaps() ? Stream.of(ops.createString("values")) : Stream.empty();
		}

		private boolean useJson(DynamicOps<?> ops) {
			return ops instanceof JsonOps && !ops.compressMaps();
		}

		@Override
		public <T> DataResult<MultipleConfiguration<V>> decode(DynamicOps<T> ops, MapLike<T> input) {
			DataResult<MapLike<T>> root = ops.compressMaps() ? ops.getMap(input.get("values")) : DataResult.success(input);
			return root.flatMap(map -> {
				ImmutableMap.Builder<String, V> successes = ImmutableMap.builder();
				ImmutableSet.Builder<String> failures = ImmutableSet.builder();
				map.entries().forEach(entry -> {
					DataResult<String> stringValue = ops.getStringValue(entry.getFirst());
					if (stringValue.result().filter(this.keyFilter).isPresent())
						stringValue.flatMap(name -> this.codec.decode(ops, entry.getSecond()).map(x -> new ImmutableTriple<>(name, x.getFirst(), entry.getSecond())))
								.resultOrPartial(failures::add)
								.ifPresent(pair -> {
									if (this.useJson(ops))
										successes.put(this.keyMapper.apply(pair.getLeft()), this.configurator.apply(pair.getLeft(), pair.getMiddle(), (JsonElement) pair.getRight()));
									else
										successes.put(pair.getLeft(), pair.getMiddle());
								});
				});
				ImmutableSet<String> build = failures.build();
				MultipleConfiguration<V> configuration = new MultipleConfiguration<>(successes.build());
				if (!build.isEmpty())
					return DataResult.error("Failed to read fields: " + String.join(", ", build), configuration);
				return DataResult.success(configuration);
			});
		}

		@Override
		public <T> RecordBuilder<T> encode(MultipleConfiguration<V> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
			RecordBuilder<T> root = ops.compressMaps() ? ops.mapBuilder() : prefix;
			input.children().forEach((key, value) -> root.add(key, this.codec.encodeStart(ops, value)));
			if (ops.compressMaps())
				prefix.add("values", root.build(ops.empty()));
			return prefix;
		}
	}
}
