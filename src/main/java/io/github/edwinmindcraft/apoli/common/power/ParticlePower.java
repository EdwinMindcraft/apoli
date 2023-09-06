package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ParticleConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticlePower extends PowerFactory<ParticleConfiguration> {

	@OnlyIn(Dist.CLIENT)
	public static void renderParticles(Entity entity, Entity camera, boolean firstPerson) {
		IPowerContainer.getPowers(entity, ApoliPowers.PARTICLE.get()).stream()
				.filter(x -> entity.tickCount % x.value().getConfiguration().frequency() == 0 && (x.value().getConfiguration().visibleInFirstPerson() || entity != camera || !firstPerson))
				.forEach(power -> entity.level().addParticle((ParticleOptions) power.value().getConfiguration().particle(), entity.getRandomX(0.5), entity.getRandomY(), entity.getRandomZ(0.5), 0, 0, 0));
	}

	public ParticlePower() {
		super(ParticleConfiguration.CODEC);
	}
}
