package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.registry.ICalioDynamicRegistryManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ParticleConfiguration(ParticleOptions particle,
                                    int frequency,
                                    boolean visibleInFirstPerson,
                                    Vec3 spread,
                                    float offsetY,
                                    int count,
                                    boolean visibleWhileInvisible,
                                    float speed) implements IDynamicFeatureConfiguration {

	public static final Codec<ParticleConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE.fieldOf("particle").forGetter(ParticleConfiguration::particle),
			Codec.intRange(1, Integer.MAX_VALUE).fieldOf("frequency").forGetter(ParticleConfiguration::frequency),
			CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "visible_in_first_person", false).forGetter(ParticleConfiguration::visibleInFirstPerson),
            CalioCodecHelper.optionalField(Vec3.CODEC, "spread", new Vec3(0.25, 0.5, 0.25)).forGetter(ParticleConfiguration::spread),
            CalioCodecHelper.optionalField(CalioCodecHelper.FLOAT, "offset_y", 1.0F).forGetter(ParticleConfiguration::offsetY),
            CalioCodecHelper.optionalField(CalioCodecHelper.INT, "count", 1).forGetter(ParticleConfiguration::count),
            CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "visible_while_invisible", false).forGetter(ParticleConfiguration::visibleWhileInvisible),
            CalioCodecHelper.optionalField(CalioCodecHelper.FLOAT, "speed", 0.0F).forGetter(ParticleConfiguration::speed)
    ).apply(instance, ParticleConfiguration::new));

	@Override
	@NotNull
	public List<String> getWarnings(@NotNull ICalioDynamicRegistryManager server) {
		return ImmutableList.of();
	}

	@Override
	public boolean isConfigurationValid() {
		return this.particle() instanceof ParticleOptions;
	}
}
