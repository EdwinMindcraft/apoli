package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.apace100.apoli.util.InventoryUtil;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.DropInventoryConfiguration;
import net.minecraft.world.entity.Entity;

public class DropInventoryAction extends EntityAction<DropInventoryConfiguration> {
    public DropInventoryAction() {
        super(DropInventoryConfiguration.CODEC);
    }

    @Override
    public void execute(DropInventoryConfiguration configuration, Entity entity) {
        InventoryUtil.dropInventory(
                configuration.slots(),
                configuration.entityAction(),
                configuration.itemCondition(),
                configuration.itemAction(),
                configuration.throwRandomly(),
                configuration.retainOwnership(),
                entity,
                configuration.power(),
                configuration.amount());
    }
}
