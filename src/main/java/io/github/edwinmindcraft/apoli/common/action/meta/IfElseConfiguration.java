package io.github.edwinmindcraft.apoli.common.action.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.configuration.MustBeBound;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

public record IfElseConfiguration<C, A, V>(@MustBeBound Holder<C> condition, @MustBeBound Holder<A> ifAction,
										   Holder<A> elseAction, BiPredicate<C, V> predicate,
										   BiConsumer<A, V> executor) implements IDelegatedActionConfiguration<V> {
	public static <C, A, V> MapCodec<IfElseConfiguration<C, A, V>> codec(CodecSet<C> conditionCodec, CodecSet<A> actionCodec, Function<String, MapCodec<Holder<A>>> optional, BiPredicate<C, V> predicate, BiConsumer<A, V> executor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				conditionCodec.holder().fieldOf("condition").forGetter(IfElseConfiguration::condition),
				actionCodec.holder().fieldOf("if_action").forGetter(IfElseConfiguration::ifAction),
				optional.apply("else_action").forGetter(IfElseConfiguration::elseAction)
		).apply(instance, (c, i, e) -> new IfElseConfiguration<>(c, i, e, predicate, executor)));
	}

	@Override
	public void execute(V parameters) {
		if (this.predicate().test(this.condition().value(), parameters))
			this.executor().accept(this.ifAction().value(), parameters);
		else if (this.elseAction().isBound())
			this.executor().accept(this.elseAction().value(), parameters);
	}
}
