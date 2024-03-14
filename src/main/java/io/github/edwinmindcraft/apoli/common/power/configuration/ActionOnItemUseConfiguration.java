package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public record ActionOnItemUseConfiguration(Holder<ConfiguredItemCondition<?, ?>> itemCondition,
										   Holder<ConfiguredEntityAction<?, ?>> entityAction,
										   Holder<ConfiguredItemAction<?, ?>> itemAction,
                                           TriggerType triggerType,
                                           int priority) implements IDynamicFeatureConfiguration {

	public static final Codec<ActionOnItemUseConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredItemCondition.optional("item_condition").forGetter(ActionOnItemUseConfiguration::itemCondition),
			ConfiguredEntityAction.optional("entity_action").forGetter(ActionOnItemUseConfiguration::entityAction),
			ConfiguredItemAction.optional("item_action").forGetter(ActionOnItemUseConfiguration::itemAction),
            ExtraCodecs.strictOptionalField(SerializableDataType.enumValue(TriggerType.class), "trigger", TriggerType.FINISH).forGetter(ActionOnItemUseConfiguration::triggerType),
            ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "priority", 0).forGetter(ActionOnItemUseConfiguration::priority)
    ).apply(instance, ActionOnItemUseConfiguration::new));

    public boolean isInPhase(ActionOnItemUseConfiguration.PriorityPhase phase) {
        return phase == PriorityPhase.ALL || (phase == PriorityPhase.BEFORE && priority() >= 0) || (phase == PriorityPhase.AFTER && priority() < 0);
    }

    public boolean doesApply(Entity player, ItemStack stack) {
        return ConfiguredItemCondition.check(this.itemCondition(), player.level(), stack);
    }

    public enum TriggerType {
        INSTANT, START, STOP, FINISH, DURING
    }

    public enum PriorityPhase {
        BEFORE, AFTER, ALL
    }

}
