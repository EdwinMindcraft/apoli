package dev.experimental.apoli.common.action.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.experimental.apoli.api.IDynamicFeatureConfiguration;
import dev.experimental.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record SpawnEntityConfiguration(EntityType<?> type, NbtCompound tag,
									   ConfiguredEntityAction<?, ?> action) implements IDynamicFeatureConfiguration {

	public static final Codec<SpawnEntityConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.ENTITY_TYPE.fieldOf("entity_type").forGetter(SpawnEntityConfiguration::type),
			SerializableDataTypes.NBT.optionalFieldOf("tag").forGetter(x -> Optional.ofNullable(x.tag())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("entity_action").forGetter(x -> Optional.ofNullable(x.action()))
	).apply(instance, (t1, t2, t3) -> new SpawnEntityConfiguration(t1, t2.orElse(null), t3.orElse(null))));

	public SpawnEntityConfiguration(@Nullable EntityType<?> type, @Nullable NbtCompound tag, @Nullable ConfiguredEntityAction<?, ?> action) {
		this.type = type;
		this.tag = tag;
		this.action = action;
	}

	@Override
	public @NotNull List<String> getErrors(@NotNull MinecraftServer server) {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		if (this.action() != null)
			builder.addAll(this.action().getErrors(server).stream().map("SpawnEntity/%s"::formatted).toList());
		return builder.build();
	}

	@Override
	public @NotNull List<String> getWarnings(@NotNull MinecraftServer server) {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		if (this.action() != null)
			builder.addAll(this.action().getWarnings(server).stream().map("SpawnEntity/%s"::formatted).toList());
		if (this.type() == null) builder.add("SpawnEntity/Missing Entity");
		return builder.build();
	}
}
