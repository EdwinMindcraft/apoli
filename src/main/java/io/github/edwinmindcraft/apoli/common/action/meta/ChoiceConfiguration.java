package io.github.edwinmindcraft.apoli.common.action.meta;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import io.github.apace100.calio.FilterableWeightedList;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.IStreamConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public record ChoiceConfiguration<T, V>(
		FilterableWeightedList<Holder<T>> list,
		BiConsumer<T, V> executor) implements IDelegatedActionConfiguration<V>, IStreamConfiguration<Holder<T>> {

	public static <T, V> MapCodec<ChoiceConfiguration<T, V>> codec(CodecSet<T> codec, BiConsumer<T, V> executor) {
		return CalioCodecHelper.weightedListOf(codec.holder()).fieldOf("actions").xmap(x -> new ChoiceConfiguration<>(x, executor), ChoiceConfiguration::list);
	}

	@Override
	public void execute(V parameters) {
		Holder<T> holder = this.list.pickRandom(new Random());
		if (holder.isBound())
			this.executor().accept(holder.value(), parameters);
	}

	@Override
	public List<String> getUnbound() {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		this.list().stream().forEach(holder -> {
			if (!holder.isBound())
				builder.add(IDynamicFeatureConfiguration.holderAsString("actions[?]/element", holder));
		});
		return builder.build();
	}

	@Override
	public List<Holder<T>> entries() {
		return this.list().stream().toList();
	}
}
