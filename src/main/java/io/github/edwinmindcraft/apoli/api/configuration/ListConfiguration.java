package io.github.edwinmindcraft.apoli.api.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;
import java.util.Optional;

public final class ListConfiguration<T> implements IDynamicFeatureConfiguration, IStreamConfiguration<T> {

	public static final MapCodec<ListConfiguration<AttributeModifier>> ATTRIBUTE_CODEC = attributeCodec("modifier");
	public static final MapCodec<ListConfiguration<ConfiguredModifier<?>>> MODIFIER_CODEC = modifierCodec("modifier");

	@SafeVarargs
	public static <T> ListConfiguration<T> of(T... elements) {
		return new ListConfiguration<>(ImmutableList.copyOf(elements));
	}

	public static MapCodec<ListConfiguration<AttributeModifier>> attributeCodec(String singular) {
		return ListConfiguration.mapCodec(SerializableDataTypes.ATTRIBUTE_MODIFIER, singular, singular + "s");
	}

	public static MapCodec<ListConfiguration<ConfiguredModifier<?>>> modifierCodec(String singular) {
		return ListConfiguration.mapCodec(ConfiguredModifier.CODEC, singular, singular + "s");
	}

	public static <T> MapCodec<ListConfiguration<T>> mapCodec(Codec<T> codec, String singular, String plural) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				ExtraCodecs.strictOptionalField(codec, singular).forGetter(ListConfiguration::getSingular),
				ExtraCodecs.strictOptionalField(CalioCodecHelper.listOf(codec), plural, ImmutableList.of()).forGetter(ListConfiguration::getMultiple)
		).apply(instance, (first, others) -> {
			ImmutableList.Builder<T> builder = ImmutableList.builder();
			first.ifPresent(builder::add);
			builder.addAll(others);
			return new ListConfiguration<>(builder.build());
		}));
	}

	public static <T> MapCodec<ListConfiguration<T>> optionalMapCodec(Codec<Optional<T>> codec, String singular, String plural) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				ExtraCodecs.strictOptionalField(codec, singular, Optional.empty()).forGetter(ListConfiguration::getSingular),
				CalioCodecHelper.optionalListOf(codec).optionalFieldOf(plural, ImmutableList.of()).forGetter(ListConfiguration::getMultiple)
		).apply(instance, (first, others) -> {
			ImmutableList.Builder<T> builder = ImmutableList.builder();
			first.ifPresent(builder::add);
			builder.addAll(others);
			return new ListConfiguration<>(builder.build());
		}));
	}

	public static <T> Codec<ListConfiguration<T>> codec(Codec<T> codec, String singular, String plural) {
		return mapCodec(codec, singular, plural).codec();
	}

	public static <T> Codec<ListConfiguration<T>> optionalCodec(Codec<Optional<T>> codec, String singular, String plural) {
		return optionalMapCodec(codec, singular, plural).codec();
	}

	private final ImmutableList<T> content;

	public ListConfiguration(Iterable<T> content) {
		this.content = ImmutableList.copyOf(content);
	}

	@SafeVarargs
	public ListConfiguration(T... params) {
		this.content = ImmutableList.copyOf(params);
	}

	private Optional<T> getSingular() {
		return this.content.size() == 1 ? this.content.stream().findFirst() : Optional.empty();
	}

	private List<T> getMultiple() {
		return this.content.size() == 1 ? ImmutableList.of() : ImmutableList.copyOf(this.content);
	}

	public List<T> getContent() {
		return this.content;
	}

	@Override
	public List<T> entries() {
		return this.content;
	}
}
