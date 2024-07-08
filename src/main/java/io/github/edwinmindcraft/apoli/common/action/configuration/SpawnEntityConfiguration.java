package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.calio.api.registry.CalioDynamicRegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record SpawnEntityConfiguration(EntityType<?> type, @Nullable CompoundTag tag,
									   Holder<ConfiguredEntityAction<?, ?>> action) implements IDynamicFeatureConfiguration {

	public static final Codec<SpawnEntityConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.ENTITY_TYPE.fieldOf("entity_type").forGetter(SpawnEntityConfiguration::type),
			SerializableDataTypes.NBT.optionalFieldOf("tag").forGetter(x -> Optional.ofNullable(x.tag())),
			ConfiguredEntityAction.optional("entity_action").forGetter(SpawnEntityConfiguration::action)
	).apply(instance, (t1, t2, t3) -> new SpawnEntityConfiguration(t1, t2.orElse(null), t3)));

	@Override
	public @NotNull List<String> getErrors(@NotNull RegistryAccess server) {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		if (this.action().isBound())
			builder.addAll(this.action().value().getErrors(server).stream().map("SpawnEntity/%s"::formatted).toList());
		return builder.build();
	}

	@Override
	public @NotNull List<String> getWarnings(@NotNull RegistryAccess server) {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		if (this.action().isBound())
			builder.addAll(this.action().value().getWarnings(server).stream().map("SpawnEntity/%s"::formatted).toList());
		return builder.build();
	}
}
