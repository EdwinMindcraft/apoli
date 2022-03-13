package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.OptionalFuncs;
import org.jetbrains.annotations.Nullable;

public record EntityActionConfiguration(@Nullable ConfiguredEntityAction<?, ?> entityAction) implements IDynamicFeatureConfiguration {
	public static final Codec<EntityActionConfiguration> CODEC = CalioCodecHelper.optionalField(ConfiguredEntityAction.CODEC, "entity_action")
			.xmap(OptionalFuncs.of(EntityActionConfiguration::new), OptionalFuncs.opt(EntityActionConfiguration::entityAction)).codec();
}
