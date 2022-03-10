package io.github.apace100.apoli.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;

public record SpawnParticlesConfiguration(ParticleOptions particle, int count, float speed, boolean force, Vec3 spread,
										  float offsetY) implements IDynamicFeatureConfiguration {
	public static final Codec<SpawnParticlesConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE.fieldOf("particle").forGetter(SpawnParticlesConfiguration::particle),
			Codec.INT.fieldOf("count").forGetter(SpawnParticlesConfiguration::count),
			CalioCodecHelper.optionalField(Codec.FLOAT, "speed", 0.0F).forGetter(SpawnParticlesConfiguration::speed),
			CalioCodecHelper.optionalField(Codec.BOOL, "force", false).forGetter(SpawnParticlesConfiguration::force),
			CalioCodecHelper.optionalField(SerializableDataTypes.VECTOR, "spread", new Vec3(0.5, 0.25, 0.5)).forGetter(SpawnParticlesConfiguration::spread),
			CalioCodecHelper.optionalField(Codec.FLOAT, "offset_y", 0.5F).forGetter(SpawnParticlesConfiguration::offsetY)
	).apply(instance, SpawnParticlesConfiguration::new));
}
