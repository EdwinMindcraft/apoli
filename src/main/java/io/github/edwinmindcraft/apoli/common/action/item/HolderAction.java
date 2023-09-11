package io.github.edwinmindcraft.apoli.common.action.item;

import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.factory.ItemAction;
import io.github.edwinmindcraft.apoli.common.registry.ApoliCapabilities;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;

public class HolderAction extends ItemAction<FieldConfiguration<Holder<ConfiguredEntityAction<?, ?>>>> {
    public HolderAction() {
        super(FieldConfiguration.codec(ConfiguredEntityAction.HOLDER, "entity_action"));
    }

    @Override
    public void execute(FieldConfiguration<Holder<ConfiguredEntityAction<?, ?>>> configuration, Level level, Mutable<ItemStack> stack) {
        if (stack.getValue().isEmpty()) return;

        stack.getValue().getCapability(ApoliCapabilities.ENTITY_LINKED_ITEM_STACK).ifPresent(eli -> {
            if (eli.getEntity() == null) return;
            ConfiguredEntityAction.execute(configuration.value(), eli.getEntity());
        });
    }
}
