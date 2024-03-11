package io.github.edwinmindcraft.apoli.common.component;

import io.github.edwinmindcraft.apoli.api.component.IPowerDataCache;
import io.github.edwinmindcraft.apoli.common.registry.ApoliCapabilities;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PowerDataCache implements IPowerDataCache, ICapabilityProvider {
	private float damage = -1F;
	private boolean shouldExecuteActions = true;

	@Override
	public void setDamage(float damage) {
		this.damage = damage;
	}

	@Override
	public float getDamage() {
		return this.damage;
	}

	@Override
	public void setShouldExecuteActions(boolean value) {
		this.shouldExecuteActions = value;
	}

	@Override
	public boolean shouldExecuteActions() {
		return this.shouldExecuteActions;
	}

	private final transient LazyOptional<IPowerDataCache> thisOptional = LazyOptional.of(() -> this);

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return ApoliCapabilities.POWER_DATA_CACHE.orEmpty(cap, this.thisOptional);
	}
}
