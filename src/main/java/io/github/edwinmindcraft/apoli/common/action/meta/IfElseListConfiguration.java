package io.github.edwinmindcraft.apoli.common.action.meta;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.IStreamConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public record IfElseListConfiguration<C, A, V>(List<Pair<Holder<C>, Holder<A>>> entries,
											   BiPredicate<C, V> predicate,
											   BiConsumer<A, V> consumer) implements IDelegatedActionConfiguration<V>, IStreamConfiguration<Pair<Holder<C>, Holder<A>>> {

	public static <C, A, V> Codec<IfElseListConfiguration<C, A, V>> codec(CodecSet<C> condition, CodecSet<A> action, BiPredicate<C, V> predicate, BiConsumer<A, V> consumer) {
		Codec<Pair<Holder<C>, Holder<A>>> pairCodec = RecordCodecBuilder.create(instance -> instance.group(
				condition.holder().fieldOf("condition").forGetter(Pair::getLeft),
				action.holder().fieldOf("action").forGetter(Pair::getRight)
		).apply(instance, Pair::of));
		return CalioCodecHelper.listOf(pairCodec).fieldOf("actions").xmap(pairs -> new IfElseListConfiguration<>(pairs, predicate, consumer), IfElseListConfiguration::entries).codec();
	}

	@Override
	public @NotNull String name() {
		return "IfElseList";
	}

	@Override
	public List<String> getUnbound() {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		for (int i = 0; i < this.entries().size(); i++) {
			Pair<Holder<C>, Holder<A>> pair = this.entries().get(i);
			if (!pair.getLeft().isBound())
				builder.add(IDynamicFeatureConfiguration.holderAsString("actions[%d]/condition".formatted(i), pair.getLeft()));
			if (!pair.getRight().isBound())
				builder.add(IDynamicFeatureConfiguration.holderAsString("actions[%d]/action".formatted(i), pair.getLeft()));
		}
		return builder.build();
	}

	@Override
	public void execute(V parameters) {
		for (Pair<Holder<C>, Holder<A>> pair : this.entries()) {
			if (this.predicate().test(pair.getKey().value(), parameters)) {
				this.consumer().accept(pair.getValue().value(), parameters);
				break;
			}
		}
	}
}
