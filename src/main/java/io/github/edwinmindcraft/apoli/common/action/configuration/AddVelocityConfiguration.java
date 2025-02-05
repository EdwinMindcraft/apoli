package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Space;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public record AddVelocityConfiguration(Vector3f direction, Space space,
									   boolean set, boolean client,
									   boolean server) implements IDynamicFeatureConfiguration {
	public static final Codec<AddVelocityConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			CalioCodecHelper.VEC3F.forGetter(AddVelocityConfiguration::direction),
			CalioCodecHelper.optionalField(ApoliDataTypes.SPACE, "space", Space.WORLD).forGetter(AddVelocityConfiguration::space),
			CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "set", false).forGetter(AddVelocityConfiguration::set),
			CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "client", true).forGetter(AddVelocityConfiguration::client),
			CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "server", true).forGetter(AddVelocityConfiguration::server)
	).apply(instance, AddVelocityConfiguration::new));
}
