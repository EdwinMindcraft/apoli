package io.github.apace100.apoli.access;

import io.github.apace100.apoli.Apoli;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * Forge: This is a capability.
 */
public interface EntityLinkedItemStack {
    ResourceLocation KEY = Apoli.identifier("entity_linked_item");
    Entity getEntity();

    void setEntity(@Nullable Entity entity);
}