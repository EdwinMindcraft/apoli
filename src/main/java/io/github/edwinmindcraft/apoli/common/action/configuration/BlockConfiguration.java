package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import net.minecraft.world.level.block.Block;

public record BlockConfiguration(Block block) implements IDynamicFeatureConfiguration {

	public static Codec<BlockConfiguration> codec(String name) {
		return SerializableDataTypes.BLOCK.fieldOf(name).xmap(BlockConfiguration::new, BlockConfiguration::block).codec();
	}
}
