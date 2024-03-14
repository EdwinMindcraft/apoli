package io.github.edwinmindcraft.apoli.common.action.meta;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.configuration.IStreamConfiguration;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.HolderSetCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

public record ExecuteMultipleConfiguration<T, V>(List<HolderSet<T>> entries,
												 BiConsumer<T, V> consumer) implements IStreamConfiguration<HolderSet<T>>, IDelegatedActionConfiguration<V> {
	public static <T, V> Codec<ExecuteMultipleConfiguration<T, V>> codec(CodecSet<T> codecs, BiConsumer<T, V> consumer) {
		return codecs.set().fieldOf("actions").xmap(x -> new ExecuteMultipleConfiguration<>(x, consumer), ExecuteMultipleConfiguration::entries).codec();
	}

	@Override
	public void execute(V parameters) {
		this.entries().stream().flatMap(HolderSet::stream).filter(Holder::isBound).forEach(holder -> this.consumer().accept(holder.value(), parameters));
	}

	@Override
	public @NotNull String name() {
		return "And";
	}
}
