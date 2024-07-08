package io.github.edwinmindcraft.apoli.common.action.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.util.Scheduler;
import io.github.edwinmindcraft.apoli.api.configuration.MustBeBound;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;

import java.util.function.BiConsumer;

public record DelayAction<T, V>(@MustBeBound Holder<T> action, int delay,
								BiConsumer<T, V> executor) implements IDelegatedActionConfiguration<V> {
	private static final Scheduler SCHEDULER = new Scheduler();

	public static <T, V> MapCodec<DelayAction<T, V>> codec(CodecSet<T> codec, BiConsumer<T, V> executor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				codec.holder().fieldOf("action").forGetter(DelayAction::action),
				CalioCodecHelper.INT.fieldOf("ticks").forGetter(DelayAction::delay)
		).apply(instance, (action, delay) -> new DelayAction<>(action, delay, executor)));
	}

	@Override
	public void execute(V parameters) {
		SCHEDULER.queue(m -> this.executor().accept(this.action().value(), parameters), this.delay());
	}
}
