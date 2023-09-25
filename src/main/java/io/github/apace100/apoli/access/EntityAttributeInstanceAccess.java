package io.github.apace100.apoli.access;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface EntityAttributeInstanceAccess {
    @Nullable Entity getEntity();
    void setEntity(Entity entity);
}
