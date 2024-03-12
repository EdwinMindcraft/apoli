package io.github.edwinmindcraft.apoli.api.power.factory.context;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record BlockConditionContext(LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter) {
}
