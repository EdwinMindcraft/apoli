package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.IntegerComparisonConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.OptionalFuncs;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public record PowerCountConfiguration(@Nullable EquipmentSlot slot,
									  IntegerComparisonConfiguration comparison) implements IDynamicFeatureConfiguration {
	public static final Codec<PowerCountConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(SerializableDataTypes.EQUIPMENT_SLOT, "slot").forGetter(OptionalFuncs.opt(PowerCountConfiguration::slot)),
			IntegerComparisonConfiguration.MAP_CODEC.forGetter(PowerCountConfiguration::comparison)
	).apply(instance, (equipmentSlot, integerComparisonConfiguration) -> new PowerCountConfiguration(equipmentSlot.orElse(null), integerComparisonConfiguration)));

	public EquipmentSlot[] target() {
		return this.slot() == null ? EquipmentSlot.values() : new EquipmentSlot[]{this.slot()};
	}
}
