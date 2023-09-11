package io.github.edwinmindcraft.apoli.common.registry;

import io.github.apace100.apoli.access.EntityLinkedItemStack;
import io.github.apace100.apoli.util.PowerGrantingItem;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.component.IPowerDataCache;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.jetbrains.annotations.NotNull;

public class ApoliCapabilities {
	@NotNull
	public static final Capability<IPowerContainer> POWER_CONTAINER = CapabilityManager.get(new CapabilityToken<>() {});

	@NotNull
	public static final Capability<IPowerDataCache> POWER_DATA_CACHE = CapabilityManager.get(new CapabilityToken<>() {});

	@NotNull
	public static final Capability<PowerGrantingItem> POWER_GRANTING_ITEM = CapabilityManager.get(new CapabilityToken<>() {});

    @NotNull
    public static final Capability<EntityLinkedItemStack> ENTITY_LINKED_ITEM_STACK = CapabilityManager.get(new CapabilityToken<>() {});
}
