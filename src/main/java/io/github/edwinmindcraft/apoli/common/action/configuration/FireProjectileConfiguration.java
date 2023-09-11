package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public record FireProjectileConfiguration(EntityType<?> entityType, float divergence,
                                          float speed, int count,
                                          Optional<CompoundTag> tag,
                                          Holder<ConfiguredEntityAction<?, ?>> projectileAction) implements IDynamicFeatureConfiguration {
    public static final Codec<FireProjectileConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SerializableDataTypes.ENTITY_TYPE.fieldOf("entity_type").forGetter(FireProjectileConfiguration::entityType),
            CalioCodecHelper.optionalField(CalioCodecHelper.FLOAT, "divergence", 1F).forGetter(FireProjectileConfiguration::divergence),
            CalioCodecHelper.optionalField(CalioCodecHelper.FLOAT, "speed", 1.5F).forGetter(FireProjectileConfiguration::speed),
            CalioCodecHelper.optionalField(CalioCodecHelper.INT, "count", 1).forGetter(FireProjectileConfiguration::count),
            CalioCodecHelper.optionalField(SerializableDataTypes.NBT, "tag").forGetter(FireProjectileConfiguration::tag),
            ConfiguredEntityAction.optional("projectile_action").forGetter(FireProjectileConfiguration::projectileAction)
    ).apply(instance, FireProjectileConfiguration::new));
}