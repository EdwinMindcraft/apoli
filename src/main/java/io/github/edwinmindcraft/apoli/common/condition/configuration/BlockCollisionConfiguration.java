package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.phys.Vec3;

public record BlockCollisionConfiguration(Vec3 offset,
                                          Holder<ConfiguredBlockCondition<?, ?>> blockCondition) implements IDynamicFeatureConfiguration {
    public static final MapCodec<BlockCollisionConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CalioCodecHelper.vec3d("offset_").forGetter(BlockCollisionConfiguration::offset),
            ConfiguredBlockCondition.optional("block_condition").forGetter(BlockCollisionConfiguration::blockCondition)
    ).apply(instance, BlockCollisionConfiguration::new));
}
