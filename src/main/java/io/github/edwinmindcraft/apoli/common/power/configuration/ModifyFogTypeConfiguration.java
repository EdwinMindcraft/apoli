package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.world.level.material.FogType;

import java.util.Optional;

public record ModifyFogTypeConfiguration(FogType to, Optional<FogType> from) implements IDynamicFeatureConfiguration {
	public static final Codec<ModifyFogTypeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.CAMERA_SUBMERSION_TYPE.fieldOf("to").forGetter(ModifyFogTypeConfiguration::to),
			ExtraCodecs.strictOptionalField(SerializableDataTypes.CAMERA_SUBMERSION_TYPE, "from").forGetter(ModifyFogTypeConfiguration::from)
	).apply(instance, ModifyFogTypeConfiguration::new));
}
