package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.apoli.util.InventoryUtil;
import io.github.apace100.calio.util.ArgumentWrapper;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.IntegerComparisonConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

import java.util.EnumSet;
import java.util.Optional;

public record InventoryConfiguration(EnumSet<InventoryUtil.InventoryType> inventoryTypes,
                                     InventoryUtil.ProcessMode processMode,
                                     Holder<ConfiguredItemCondition<?, ?>> itemCondition,
                                     ListConfiguration<ArgumentWrapper<Integer>> slots,
                                     Optional<ResourceKey<ConfiguredPower<?, ?>>> power,
                                     IntegerComparisonConfiguration comparison) implements IDynamicFeatureConfiguration {

    public static final MapCodec<InventoryConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ApoliDataTypes.INVENTORY_TYPE_SET.optionalFieldOf("inventory_types", EnumSet.of(InventoryUtil.InventoryType.INVENTORY)).forGetter(InventoryConfiguration::inventoryTypes),
            ApoliDataTypes.PROCESS_MODE.optionalFieldOf("process_mode", InventoryUtil.ProcessMode.ITEMS).forGetter(InventoryConfiguration::processMode),
            ConfiguredItemCondition.optional("item_condition").forGetter(InventoryConfiguration::itemCondition),
            ListConfiguration.mapCodec(ApoliDataTypes.ITEM_SLOT, "slot", "slots").forGetter(InventoryConfiguration::slots),
            CalioCodecHelper.resourceKey(ApoliDynamicRegistries.CONFIGURED_POWER_KEY).optionalFieldOf("power").forGetter(InventoryConfiguration::power),
            IntegerComparisonConfiguration.withDefaults(Comparison.GREATER_THAN_OR_EQUAL, 0).forGetter(InventoryConfiguration::comparison)
    ).apply(instance, InventoryConfiguration::new));

}