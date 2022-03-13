package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.IntegerComparisonConfiguration;
import io.github.edwinmindcraft.apoli.common.condition.configuration.PowerCountConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.OptionalFuncs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public record ItemHasPowerConfiguration(EquipmentSlot slot, ResourceLocation power) implements IDynamicFeatureConfiguration {
	public static final Codec<ItemHasPowerConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			CalioCodecHelper.optionalField(SerializableDataTypes.EQUIPMENT_SLOT, "slot").forGetter(OptionalFuncs.opt(ItemHasPowerConfiguration::slot)),
			SerializableDataTypes.IDENTIFIER.fieldOf("power").forGetter(ItemHasPowerConfiguration::power)
	).apply(instance, (equipmentSlot, integerComparisonConfiguration) -> new ItemHasPowerConfiguration(equipmentSlot.orElse(null), integerComparisonConfiguration)));

	public EquipmentSlot[] target() {
		return this.slot() == null ? EquipmentSlot.values() : new EquipmentSlot[] {this.slot()};
	}
}
