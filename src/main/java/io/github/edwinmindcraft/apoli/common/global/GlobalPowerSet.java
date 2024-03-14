package io.github.edwinmindcraft.apoli.common.global;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.calio.util.TagLike;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record GlobalPowerSet(int order,
							 Optional<TagLike<EntityType<?>>> entityTypes,
							 List<ResourceKey<ConfiguredPower<?, ?>>> powers) implements Comparable<GlobalPowerSet> {
	public static final Codec<GlobalPowerSet> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "order", 0).forGetter(GlobalPowerSet::order),
			ExtraCodecs.strictOptionalField(SerializableDataTypes.ENTITY_TYPE_TAG_LIKE, "entity_types").forGetter(GlobalPowerSet::entityTypes),
			CalioCodecHelper.listOf(ResourceKey.codec(ApoliDynamicRegistries.CONFIGURED_POWER_KEY)).fieldOf("powers").forGetter(GlobalPowerSet::powers)
	).apply(instance, GlobalPowerSet::new));

	public boolean doesApply(EntityType<?> entityType) {
		return entityTypes.isEmpty() || entityTypes.get().contains(entityType);
	}

	public boolean doesApply(Entity entity) {
		return doesApply(entity.getType());
	}

	@Override
	public int compareTo(@NotNull GlobalPowerSet o) {
		return Integer.compare(order, o.order);
	}
}