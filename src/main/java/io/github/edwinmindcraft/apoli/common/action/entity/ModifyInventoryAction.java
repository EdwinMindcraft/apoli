package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.apace100.apoli.util.InventoryUtil;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.ModifyInventoryConfiguration;
import net.minecraft.world.entity.Entity;

public class ModifyInventoryAction extends EntityAction<ModifyInventoryConfiguration> {
    public ModifyInventoryAction() {
        super(ModifyInventoryConfiguration.CODEC);
    }

    @Override
    public void execute(ModifyInventoryConfiguration configuration, Entity entity) {
        InventoryUtil.modifyInventory(
                configuration.slots(),
                configuration.entityAction(),
                configuration.itemCondition(),
                configuration.itemAction(),
                entity,
                configuration.power(),
                configuration.processMode().getProcessor(),
                configuration.limit());
    }
}
