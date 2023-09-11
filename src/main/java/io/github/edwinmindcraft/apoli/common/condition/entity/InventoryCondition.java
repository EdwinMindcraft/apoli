package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.apace100.apoli.util.InventoryUtil;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.InventoryConfiguration;
import io.github.edwinmindcraft.apoli.common.power.InventoryPower;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;

public class InventoryCondition extends EntityCondition<InventoryConfiguration> {

    public InventoryCondition() {
        super(InventoryConfiguration.CODEC);
    }

    protected boolean check(InventoryConfiguration configuration, Entity entity) {

        boolean result = false;
        int matches = 0;

        if (configuration.inventoryTypes().contains(InventoryUtil.InventoryType.INVENTORY)) {
            matches += InventoryUtil.checkInventory(configuration.itemCondition(), configuration.slots(),  entity, null, configuration.processMode().getProcessor());
            result = configuration.comparison().check(matches);
        }
        if (configuration.inventoryTypes().contains(InventoryUtil.InventoryType.POWER)) {
            if (configuration.power().isEmpty()) {
                return result;
            }

            IPowerContainer container = ApoliAPI.getPowerContainer(entity);
            if (container == null) {
                return result;
            }

            Holder<ConfiguredPower<?, ?>> targetPower = (Holder<ConfiguredPower<?, ?>>)(Object)container.getPower(configuration.power().get());
            if (targetPower == null || !targetPower.isBound() || !(targetPower.value().getFactory() instanceof InventoryPower)) {
                return result;
            }
            ConfiguredPower<io.github.edwinmindcraft.apoli.common.power.configuration.InventoryConfiguration, InventoryPower> inventoryPower = (ConfiguredPower<io.github.edwinmindcraft.apoli.common.power.configuration.InventoryConfiguration, InventoryPower>)targetPower.value();

            matches += InventoryUtil.checkInventory(configuration.itemCondition(), configuration.slots(), entity, inventoryPower, configuration.processMode().getProcessor());
            result = configuration.comparison().check(matches);

        }

        return result;

    }
}
