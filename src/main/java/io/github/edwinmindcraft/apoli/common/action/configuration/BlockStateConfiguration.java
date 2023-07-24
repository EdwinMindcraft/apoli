package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import net.minecraft.world.level.block.state.BlockState;

public record BlockStateConfiguration(BlockState state) implements IDynamicFeatureConfiguration {

	public static Codec<BlockStateConfiguration> codec(String name) {
		return SerializableDataTypes.BLOCK_STATE.fieldOf(name).xmap(BlockStateConfiguration::new, BlockStateConfiguration::state).codec();
	}
}
