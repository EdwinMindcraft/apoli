package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ParticleConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticlePower extends PowerFactory<ParticleConfiguration> {

	@OnlyIn(Dist.CLIENT)
	public static void renderParticles(Entity entity, Entity camera, boolean firstPerson) {
		IPowerContainer.getPowers(entity, ApoliPowers.PARTICLE.get()).stream()
				.filter(x -> entity.tickCount % x.value().getConfiguration().frequency() == 0 && (x.value().getConfiguration().visibleInFirstPerson() || entity != camera || !firstPerson) && x.value().getConfiguration().count() > 0)
				.forEach(power -> {
                    Vec3 spread = power.value().getConfiguration().spread();
                    entity.level().addParticle(power.value().getConfiguration().particle(), entity.getX() + entity.level().getRandom().nextGaussian() + spread.x(), power.value().getConfiguration().offsetY() + entity.getY() + entity.level().getRandom().nextGaussian() + spread.y(), entity.getZ() + entity.level().getRandom().nextGaussian() + spread.z(), (2.0 * entity.level().getRandom().nextGaussian() - 1.0) * power.value().getConfiguration().speed(), (2.0 * entity.level().getRandom().nextGaussian() - 1.0) * power.value().getConfiguration().speed(), (2.0 * entity.level().getRandom().nextGaussian() - 1.0) * power.value().getConfiguration().speed());
                });
	}

	public ParticlePower() {
		super(ParticleConfiguration.CODEC);
	}
}
