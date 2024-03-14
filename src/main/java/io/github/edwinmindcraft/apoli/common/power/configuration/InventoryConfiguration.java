package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.common.power.InventoryPower;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;

public record InventoryConfiguration(String inventoryName, boolean dropOnDeath,
									 InventoryPower.ContainerType containerType,
									 Holder<ConfiguredItemCondition<?, ?>> dropFilter,
									 IActivePower.Key key, boolean recoverable) implements IDynamicFeatureConfiguration {
	public static final Codec<InventoryConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(Codec.STRING, "title", "container.inventory").forGetter(InventoryConfiguration::inventoryName),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "drop_on_death", false).forGetter(InventoryConfiguration::dropOnDeath),
			ExtraCodecs.strictOptionalField(SerializableDataType.enumValue(InventoryPower.ContainerType.class), "container_type", InventoryPower.ContainerType.DROPPER).forGetter(InventoryConfiguration::containerType),
			ConfiguredItemCondition.optional("drop_on_death_filter").forGetter(InventoryConfiguration::dropFilter),
			ExtraCodecs.strictOptionalField(IActivePower.Key.BACKWARD_COMPATIBLE_CODEC, "key", IActivePower.Key.PRIMARY).forGetter(InventoryConfiguration::key),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "recoverable", true).forGetter(InventoryConfiguration::recoverable)
	).apply(instance, InventoryConfiguration::new));
}
