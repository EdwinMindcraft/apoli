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
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ReplaceInventoryConfiguration(InventoryUtil.InventoryType inventoryType,
                                            Holder<ConfiguredEntityAction<?, ?>> entityAction,
                                            Holder<ConfiguredItemAction<?, ?>> itemAction,
                                            Holder<ConfiguredItemCondition<?, ?>> itemCondition,
                                            ListConfiguration<ArgumentWrapper<Integer>> slots,
                                            Optional<ResourceLocation> power,
                                            ItemStack stack,
                                            boolean mergeNbt) implements IDynamicFeatureConfiguration {

    public static final Codec<ReplaceInventoryConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.strictOptionalField(ApoliDataTypes.INVENTORY_TYPE, "inventory_type", InventoryUtil.InventoryType.INVENTORY).forGetter(ReplaceInventoryConfiguration::inventoryType),
            ConfiguredEntityAction.optional("entity_action").forGetter(ReplaceInventoryConfiguration::entityAction),
            ConfiguredItemAction.optional("item_action").forGetter(ReplaceInventoryConfiguration::itemAction),
            ConfiguredItemCondition.optional("item_condition").forGetter(ReplaceInventoryConfiguration::itemCondition),
            ListConfiguration.mapCodec(ApoliDataTypes.ITEM_SLOT, "slot", "slots").forGetter(ReplaceInventoryConfiguration::slots),
            ExtraCodecs.strictOptionalField(SerializableDataTypes.IDENTIFIER, "power").forGetter(ReplaceInventoryConfiguration::power),
            SerializableDataTypes.ITEM_STACK.fieldOf("stack").forGetter(ReplaceInventoryConfiguration::stack),
            ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "merge_nbt", false).forGetter(ReplaceInventoryConfiguration::mergeNbt)
    ).apply(instance, ReplaceInventoryConfiguration::new));

}
