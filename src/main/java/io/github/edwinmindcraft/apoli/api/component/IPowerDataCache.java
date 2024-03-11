package io.github.edwinmindcraft.apoli.api.component;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.common.registry.ApoliCapabilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;

public interface IPowerDataCache {
	ResourceLocation KEY = Apoli.identifier("power_data");

	static LazyOptional<IPowerDataCache> get(LivingEntity entity) {
		return entity.getCapability(ApoliCapabilities.POWER_DATA_CACHE);
	}

	void setDamage(float damage);

	float getDamage();

	void setShouldExecuteActions(boolean value);

	boolean shouldExecuteActions();
}
