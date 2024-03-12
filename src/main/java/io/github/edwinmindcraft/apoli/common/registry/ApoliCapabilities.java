package io.github.edwinmindcraft.apoli.common.registry;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.access.EntityLinkedItemStack;
import io.github.apace100.apoli.util.PowerGrantingItem;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import net.neoforged.neoforge.capabilities.EntityCapability;
import org.jetbrains.annotations.NotNull;

public class ApoliCapabilities {
	@NotNull
	public static final EntityCapability<PowerContainer, Void> POWER_CONTAINER = EntityCapability.createVoid(Apoli.identifier("power_container"), PowerContainer.class);
	@NotNull
	public static final EntityCapability<PowerDataCache, Void> POWER_DATA_CACHE = EntityCapability.createVoid(Apoli.identifier("power_data_cache"), PowerDataCache.class);
	@NotNull
	public static final EntityCapability<PowerGrantingItem, Void> POWER_GRANTING_ITEM = EntityCapability.createVoid(Apoli.identifier("power_granting_item"), PowerGrantingItem.class);
    @NotNull
    public static final EntityCapability<EntityLinkedItemStack, Void> ENTITY_LINKED_ITEM_STACK = EntityCapability.createVoid(Apoli.identifier("entity_linked_item_stack"), EntityLinkedItemStack.class);
}
