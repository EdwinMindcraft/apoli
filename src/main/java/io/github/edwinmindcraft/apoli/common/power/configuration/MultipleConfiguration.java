package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import net.minecraft.core.Holder;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

//Validation occurs on every subpower, this should be fine
public record MultipleConfiguration<V>(Map<String, Holder<V>> children) implements IDynamicFeatureConfiguration {

	public static <V> MapCodec<MultipleConfiguration<V>> mapCodec(Codec<V> codec, Codec<Holder<V>> holderCodec, Predicate<String> filter, UnaryOperator<String> keyMapper, Function3<String, V, JsonElement, V> configurator) {
		return new MultipleMapCodec<>(codec, holderCodec, filter, keyMapper, configurator);
	}

	private static final class MultipleMapCodec<V> extends MapCodec<MultipleConfiguration<V>> {
		private final Codec<V> codec;
		private final Codec<Holder<V>> holderCodec;
		private final Predicate<String> keyFilter;
		private final UnaryOperator<String> keyMapper;
		private final Function3<String, V, JsonElement, V> configurator;

		private MultipleMapCodec(Codec<V> codec, Codec<Holder<V>> holderCodec, Predicate<String> keyFilter, UnaryOperator<String> keyMapper, Function3<String, V, JsonElement, V> configurator) {
			this.codec = codec;
			this.holderCodec = holderCodec;
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
				ImmutableMap.Builder<String, Holder<V>> successes = ImmutableMap.builder();
				ImmutableSet.Builder<String> failures = ImmutableSet.builder();
				map.entries().forEach(entry -> {
					DataResult<String> stringValue = ops.getStringValue(entry.getFirst());
					if (stringValue.result().filter(this.keyFilter).isPresent()) {
						stringValue.flatMap(name -> {
							if (this.useJson(ops))
								return this.codec.decode(ops, entry.getSecond())
										.map(pair -> Pair.of(this.keyMapper.apply(name), Holder.direct(this.configurator.apply(name, pair.getFirst(), (JsonElement) pair.getSecond()))));
							else
								return this.holderCodec.decode(ops, entry.getSecond()).map(pair -> Pair.of(name, pair.getFirst()));
						}).resultOrPartial(failures::add).ifPresent(pair -> successes.put(pair.getFirst(), pair.getSecond()));
					}
				});
				ImmutableSet<String> build = failures.build();
				MultipleConfiguration<V> configuration = new MultipleConfiguration<>(successes.build());
				if (!build.isEmpty())
					return DataResult.error(() -> "Failed to read fields: " + String.join(", ", build), configuration);
				return DataResult.success(configuration);
			});
		}

		@Override
		public <T> RecordBuilder<T> encode(MultipleConfiguration<V> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
			RecordBuilder<T> root = ops.compressMaps() ? ops.mapBuilder() : prefix;
			input.children().forEach((key, value) -> root.add(key, this.useJson(ops) ? this.codec.encodeStart(ops, value.value()) : this.holderCodec.encodeStart(ops, value)));
			if (ops.compressMaps())
				prefix.add("values", root.build(ops.empty()));
			return prefix;
		}
	}
}
