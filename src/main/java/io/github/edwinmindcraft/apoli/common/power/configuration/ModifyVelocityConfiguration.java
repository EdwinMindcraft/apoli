package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IValueModifyingPowerConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Direction;

import java.util.EnumSet;

public record ModifyVelocityConfiguration(ListConfiguration<ConfiguredModifier<?>> modifiers,
										  EnumSet<Direction.Axis> axes) implements IValueModifyingPowerConfiguration {

	public static final Codec<ModifyVelocityConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.MODIFIER_CODEC.forGetter(ModifyVelocityConfiguration::modifiers),
			ExtraCodecs.strictOptionalField(SerializableDataTypes.AXIS_SET, "axes", EnumSet.allOf(Direction.Axis.class)).forGetter(ModifyVelocityConfiguration::axes)
	).apply(instance, ModifyVelocityConfiguration::new));
}
