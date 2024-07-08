package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;

public record EquippedItemConfiguration(EquipmentSlot slot,
										Holder<ConfiguredItemCondition<?, ?>> condition) implements IDynamicFeatureConfiguration {
	public static final MapCodec<EquippedItemConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			SerializableDataTypes.EQUIPMENT_SLOT.fieldOf("equipment_slot").forGetter(EquippedItemConfiguration::slot),
			ConfiguredItemCondition.required("item_condition").forGetter(EquippedItemConfiguration::condition)
	).apply(instance, EquippedItemConfiguration::new));
}
