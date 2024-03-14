package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.DamageSourceDescription;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

import java.util.Optional;

public record DamageConfiguration(Optional<ResourceKey<DamageType>> damageType, Optional<DamageSourceDescription> source, float amount) implements IDynamicFeatureConfiguration {
	public static final Codec<DamageConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.strictOptionalField(SerializableDataTypes.DAMAGE_TYPE, "damage_type").forGetter(DamageConfiguration::damageType),
            ExtraCodecs.strictOptionalField(ApoliDataTypes.DAMAGE_SOURCE_DESCRIPTION, "source").forGetter(DamageConfiguration::source),
			CalioCodecHelper.FLOAT.fieldOf("amount").forGetter(DamageConfiguration::amount)
	).apply(instance, DamageConfiguration::new));
}
