package io.github.edwinmindcraft.apoli.common.power;

import io.github.apace100.apoli.power.Prioritized;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ActionOnItemUseConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.Mutable;

import java.util.List;

public class ActionOnItemUsePower extends PowerFactory<ActionOnItemUseConfiguration> implements Prioritized<ActionOnItemUseConfiguration, ActionOnItemUsePower> {
	public static void execute(Entity player, ItemStack stack, Mutable<ItemStack> target, ActionOnItemUseConfiguration.TriggerType triggerType, ActionOnItemUseConfiguration.PriorityPhase phase) {
        if (player.level().isClientSide() || ApoliAPI.getPowerContainer(player) == null || !ApoliAPI.getPowerContainer(player).hasPower(ApoliPowers.ACTION_ON_ITEM_USE.get())) return;
        Prioritized.CallInstance<ActionOnItemUseConfiguration, ActionOnItemUsePower> callInstance = new Prioritized.CallInstance<>();
        callInstance.add(player, ApoliPowers.ACTION_ON_ITEM_USE.get(), p -> p.value().getFactory().canRun(p, player, stack, triggerType, phase));
        for (int i = callInstance.getMaxPriority(); i >= callInstance.getMinPriority(); i--) {
            if (!callInstance.hasPowers(i)) {
                continue;
            }
            List<Holder<ConfiguredPower<ActionOnItemUseConfiguration, ActionOnItemUsePower>>> configurations = callInstance.getPowers(i);
            for (Holder<ConfiguredPower<ActionOnItemUseConfiguration, ActionOnItemUsePower>> configuration : configurations) {
                ConfiguredItemAction.execute(configuration.value().getConfiguration().itemAction(), player.level(), target);
                ConfiguredEntityAction.execute(configuration.value().getConfiguration().entityAction(), player);
            }
        }
	}

    public boolean canRun(Holder<ConfiguredPower<ActionOnItemUseConfiguration, ActionOnItemUsePower>> p, Entity player, ItemStack stack, ActionOnItemUseConfiguration.TriggerType triggerType, ActionOnItemUseConfiguration.PriorityPhase phase) {
        return p.value().getConfiguration().triggerType() == triggerType && p.value().getConfiguration().doesApply(player, stack) && p.value().getConfiguration().isInPhase(phase);
    }

	public ActionOnItemUsePower() {
		super(ActionOnItemUseConfiguration.CODEC);
	}

    @Override
    public int getPriority(ActionOnItemUseConfiguration configuration) {
        return configuration.priority();
    }
}
