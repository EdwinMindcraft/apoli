package io.github.edwinmindcraft.apoli.common.component;

import io.github.apace100.apoli.access.EntityLinkedItemStack;
import io.github.edwinmindcraft.apoli.common.registry.ApoliCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityLinkedItemStackImpl implements EntityLinkedItemStack, ICapabilityProvider {

    private final ItemStack owner;
    @Nullable private Entity entity;
    private final transient LazyOptional<EntityLinkedItemStack> thisOptional = LazyOptional.of(() -> this);

    public EntityLinkedItemStackImpl(ItemStack owner) {
        this.owner = owner;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ApoliCapabilities.ENTITY_LINKED_ITEM_STACK.orEmpty(cap, thisOptional);
    }

    @Override
    public Entity getEntity() {
        Entity vanillaHolder = owner.getEntityRepresentation();
        if (vanillaHolder == null) {
            return this.entity;
        }
        return vanillaHolder;
    }

    @Override
    public void setEntity(@Nullable Entity entity) {
        this.entity = entity;
    }
}
