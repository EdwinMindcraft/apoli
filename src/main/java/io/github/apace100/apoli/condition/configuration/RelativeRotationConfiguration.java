package io.github.apace100.apoli.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.power.factory.condition.bientity.RelativeRotationCondition.RotationType;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.DoubleComparisonConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Direction;

import java.util.EnumSet;

public record RelativeRotationConfiguration(EnumSet<Direction.Axis> axes, RotationType actorRotation,
											RotationType targetRotation,
											DoubleComparisonConfiguration comparison) implements IDynamicFeatureConfiguration {
	public static final Codec<RelativeRotationConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(SerializableDataTypes.AXIS_SET, "axes", EnumSet.allOf(Direction.Axis.class)).forGetter(RelativeRotationConfiguration::axes),
			ExtraCodecs.strictOptionalField(SerializableDataType.enumValue(RotationType.class), "actor_rotation", RotationType.HEAD).forGetter(RelativeRotationConfiguration::actorRotation),
			ExtraCodecs.strictOptionalField(SerializableDataType.enumValue(RotationType.class), "target_rotation", RotationType.BODY).forGetter(RelativeRotationConfiguration::targetRotation),
			DoubleComparisonConfiguration.MAP_CODEC.forGetter(RelativeRotationConfiguration::comparison)
	).apply(instance, RelativeRotationConfiguration::new));
}
