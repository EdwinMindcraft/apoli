package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.apace100.apoli.util.InventoryUtil;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.ReplaceInventoryConfiguration;
import net.minecraft.world.entity.Entity;

public class ReplaceInventoryAction extends EntityAction<ReplaceInventoryConfiguration> {
    public ReplaceInventoryAction() {
        super(ReplaceInventoryConfiguration.CODEC);
    }

    @Override
    public void execute(ReplaceInventoryConfiguration configuration, Entity entity) {
        InventoryUtil.replaceInventory(
                configuration.slots(),
                configuration.stack(),
                configuration.entityAction(),
                configuration.itemCondition(),
                configuration.itemAction(),
                entity,
                configuration.power(),
                configuration.mergeNbt());
    }
}
