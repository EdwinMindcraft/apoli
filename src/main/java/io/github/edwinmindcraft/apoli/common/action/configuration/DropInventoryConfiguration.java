package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.InventoryUtil;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.calio.util.ArgumentWrapper;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record DropInventoryConfiguration(InventoryUtil.InventoryType inventoryType,
                                         Holder<ConfiguredEntityAction<?, ?>> entityAction,
                                         Holder<ConfiguredItemAction<?, ?>> itemAction,
                                         Holder<ConfiguredItemCondition<?, ?>> itemCondition,
                                         ListConfiguration<ArgumentWrapper<Integer>> slots,
                                         Optional<ResourceLocation> power,
                                         boolean throwRandomly,
                                         boolean retainOwnership,
                                         int amount) implements IDynamicFeatureConfiguration {

    public static final Codec<DropInventoryConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CalioCodecHelper.optionalField(SerializableDataType.enumValue(InventoryUtil.InventoryType.class), "inventory_type", InventoryUtil.InventoryType.INVENTORY).forGetter(DropInventoryConfiguration::inventoryType),
            ConfiguredEntityAction.optional("entity_action").forGetter(DropInventoryConfiguration::entityAction),
            ConfiguredItemAction.optional("item_action").forGetter(DropInventoryConfiguration::itemAction),
            ConfiguredItemCondition.optional("item_condition").forGetter(DropInventoryConfiguration::itemCondition),
            ListConfiguration.mapCodec(ApoliDataTypes.ITEM_SLOT, "slot", "slots").forGetter(DropInventoryConfiguration::slots),
            CalioCodecHelper.optionalField(SerializableDataTypes.IDENTIFIER, "power").forGetter(DropInventoryConfiguration::power),
            CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "throw_randomly", false).forGetter(DropInventoryConfiguration::throwRandomly),
            CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "retain_ownership", true).forGetter(DropInventoryConfiguration::retainOwnership),
            CalioCodecHelper.optionalField(CalioCodecHelper.INT, "amount", 0).forGetter(DropInventoryConfiguration::amount)
    ).apply(instance, DropInventoryConfiguration::new));

}
