package io.github.apace100.apoli.power.factory.action.entity;

import io.github.apace100.apoli.action.configuration.SpawnParticlesConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SpawnParticlesAction extends EntityAction<SpawnParticlesConfiguration> {

	public SpawnParticlesAction() {
		super(SpawnParticlesConfiguration.CODEC);
	}

	@Override
	public void execute(@NotNull SpawnParticlesConfiguration configuration, @NotNull Entity entity) {
		if (entity.level().isClientSide() || !(entity.level() instanceof ServerLevel level)) {
			return;
		}
		if (configuration.count() <= 0)
			return;
		Vec3 spread = configuration.spread();
		float deltaX = (float) (entity.getBbWidth() * spread.x());
		float deltaY = (float) (entity.getBbHeight() * spread.y());
		float deltaZ = (float) (entity.getBbWidth() * spread.z());
		float offsetY = entity.getBbHeight() * configuration.offsetY();
		level.sendParticles(configuration.particle(), entity.getX(), entity.getY() + offsetY, entity.getZ(), configuration.count(), deltaX, deltaY, deltaZ, configuration.speed());
	}
}
