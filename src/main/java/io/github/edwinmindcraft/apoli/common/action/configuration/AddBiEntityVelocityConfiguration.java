package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import org.joml.Vector3f;

public record AddBiEntityVelocityConfiguration(Vector3f direction, boolean client, boolean server,
                                               boolean set) implements IDynamicFeatureConfiguration {
	public static final Codec<AddBiEntityVelocityConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			CalioCodecHelper.VEC3F.forGetter(AddBiEntityVelocityConfiguration::direction),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "client", true).forGetter(AddBiEntityVelocityConfiguration::client),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "server", true).forGetter(AddBiEntityVelocityConfiguration::server),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "set", false).forGetter(AddBiEntityVelocityConfiguration::set)
	).apply(instance, AddBiEntityVelocityConfiguration::new));
}
