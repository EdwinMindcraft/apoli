package io.github.edwinmindcraft.apoli.api.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.tags.TagKey;

import java.util.Objects;
import java.util.Optional;

public record TagConfiguration<V>(TagKey<V> value) implements IDynamicFeatureConfiguration {

	public static <T> Codec<TagConfiguration<T>> codec(Codec<TagKey<T>> codec, String fieldName) {
		return codec.fieldOf(fieldName).xmap(TagConfiguration::new, TagConfiguration::value).codec();
	}

	public static <T> MapCodec<Optional<TagConfiguration<T>>> optionalField(Codec<TagKey<T>> codec, String fieldName) {
		return ExtraCodecs.strictOptionalField(codec, fieldName).xmap(x -> x.map(TagConfiguration::new), x -> x.map(TagConfiguration::value));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (TagConfiguration<?>) obj;
		return Objects.equals(this.value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.value);
	}

	@Override
	public String toString() {
		return "TagConfiguration[" +
			   "value=" + this.value + ']';
	}
}
