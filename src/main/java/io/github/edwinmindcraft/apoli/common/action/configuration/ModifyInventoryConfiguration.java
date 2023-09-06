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

public record ModifyInventoryConfiguration(InventoryUtil.InventoryType inventoryType,
                                           InventoryUtil.ProcessMode processMode,
                                           Holder<ConfiguredEntityAction<?, ?>> entityAction,
                                           Holder<ConfiguredItemAction<?, ?>> itemAction,
                                           Holder<ConfiguredItemCondition<?, ?>> itemCondition,
                                           ListConfiguration<ArgumentWrapper<Integer>> slots,
                                           Optional<ResourceLocation> power,
                                           int limit) implements IDynamicFeatureConfiguration {

    public static final Codec<ModifyInventoryConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CalioCodecHelper.optionalField(SerializableDataType.enumValue(InventoryUtil.InventoryType.class), "inventory_type", InventoryUtil.InventoryType.INVENTORY).forGetter(ModifyInventoryConfiguration::inventoryType),
            CalioCodecHelper.optionalField(ApoliDataTypes.PROCESS_MODE, "process_mode", InventoryUtil.ProcessMode.STACKS).forGetter(ModifyInventoryConfiguration::processMode),
            ConfiguredEntityAction.optional("entity_action").forGetter(ModifyInventoryConfiguration::entityAction),
            ConfiguredItemAction.optional("item_action").forGetter(ModifyInventoryConfiguration::itemAction),
            ConfiguredItemCondition.optional("item_condition").forGetter(ModifyInventoryConfiguration::itemCondition),
            ListConfiguration.mapCodec(ApoliDataTypes.ITEM_SLOT, "slot", "slots").forGetter(ModifyInventoryConfiguration::slots),
            CalioCodecHelper.optionalField(SerializableDataTypes.IDENTIFIER, "power").forGetter(ModifyInventoryConfiguration::power),
            CalioCodecHelper.optionalField(CalioCodecHelper.INT, "limit", 1).forGetter(ModifyInventoryConfiguration::limit)
    ).apply(instance, ModifyInventoryConfiguration::new));

}
