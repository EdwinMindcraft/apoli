package io.github.apace100.apoli.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.util.Shape;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.MustBeBound;
import io.github.edwinmindcraft.apoli.api.power.ConfiguredCondition;
import io.github.edwinmindcraft.apoli.api.power.ConfiguredFactory;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;

public record AreaOfEffectConfiguration<A extends ConfiguredFactory<?, ?, ?>, C extends ConfiguredCondition<?, ?, ?>>(double radius, @MustBeBound Holder<A> action,
                                                                                                             Holder<C> condition,
                                                                                                             Shape shape,
                                                                                                             boolean includeTarget) implements IDynamicFeatureConfiguration {
    public static <A extends ConfiguredFactory<?, ?, ?>, C extends ConfiguredCondition<?, ?, ?>> Codec<AreaOfEffectConfiguration<A, C>> createCodec(MapCodec<Holder<A>> actionCodec, MapCodec<Holder<C>> conditionCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.strictOptionalField(CalioCodecHelper.DOUBLE, "radius", 16.0).forGetter(AreaOfEffectConfiguration::radius),
                actionCodec.forGetter(AreaOfEffectConfiguration::action),
                conditionCodec.forGetter(AreaOfEffectConfiguration::condition),
                ExtraCodecs.strictOptionalField(SerializableDataType.enumValue(Shape.class), "shape", Shape.CUBE).forGetter(AreaOfEffectConfiguration::shape),
                ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "include_target", false).forGetter(AreaOfEffectConfiguration::includeTarget)
        ).apply(instance, AreaOfEffectConfiguration::new));
    }
}
