package io.github.edwinmindcraft.apoli.common.condition.block;

import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.BlockStateConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;


import java.util.Collection;
import java.util.function.Supplier;

public class BlockStateCondition extends BlockCondition<BlockStateConfiguration> {

	public BlockStateCondition() {
		super(BlockStateConfiguration.CODEC);
	}

	@Override
	protected boolean check(BlockStateConfiguration configuration, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter) {
		BlockState state = stateGetter.get();
		Collection<Property<?>> properties = state.getProperties();
		return properties.stream().filter(p -> configuration.property().equals(p.getName())).findFirst().map(property -> configuration.checkProperty(state.getValue(property))).orElse(false);
	}
}

