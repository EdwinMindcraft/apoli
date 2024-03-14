package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public record ProjectileConfiguration(Optional<EntityType<?>> projectile,
                                     Holder<ConfiguredEntityCondition<?, ?>> projectileCondition) implements IDynamicFeatureConfiguration {
    public static final Codec<ProjectileConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.strictOptionalField(SerializableDataTypes.ENTITY_TYPE, "projectile").forGetter(ProjectileConfiguration::projectile),
            ConfiguredEntityCondition.optional("projectile_condition").forGetter(ProjectileConfiguration::projectileCondition)
    ).apply(instance, ProjectileConfiguration::new));
}