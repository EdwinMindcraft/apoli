package io.github.edwinmindcraft.apoli.api.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A generic type to define an action.
 *
 * @param <T> The type of the {@link IDynamicFeatureConfiguration} this factory will accept.
 * @param <C> The type of the {@link ConfiguredFactory} this factory will instantiate.
 * @param <F> The type of this {@link IFactory}.
 */
public interface IFactory<T extends IDynamicFeatureConfiguration, C extends ConfiguredFactory<T, ? extends F, ?>, F extends IFactory<T, C, F>> {

	/**
	 * Gets or create a {@link MapCodec} from the given {@link Codec}
	 *
	 * @param codec The codec to transform
	 *
	 * @return Either the codec itself if it was a boxed MapCodec, or a field with name "value"
	 */
	static <T> MapCodec<T> asMap(Codec<T> codec) {
		if (codec instanceof MapCodec.MapCodecCodec)
			return ((MapCodec.MapCodecCodec<T>) codec).codec();
		return codec.fieldOf("value");
	}

	static <T, V, R> MapCodec<R> unionCodec(MapCodec<T> first, MapCodec<V> second, BiFunction<T, V, R> function, Function<R, T> firstGetter, Function<R, V> secondGetter) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				first.forGetter(firstGetter),
				second.forGetter(secondGetter)
		).apply(instance, function));
	}

	static <T, R> MapCodec<R> singleCodec(MapCodec<T> first, Function<T, R> to, Function<R, T> from) {
		return first.xmap(to, from);
	}

	/**
	 * Configures a new {@link ConfiguredFactory} from the given parameters.
	 *
	 * @param input The configuration to apply to this factory.
	 *
	 * @return A a {@link ConfiguredFactory} with this factory and the given configuration.
	 */
	C configure(T input);
}
