package io.github.edwinmindcraft.apoli.api.power.factory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.IConditionFactory;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class BlockCondition<T extends IDynamicFeatureConfiguration> implements IConditionFactory<T, ConfiguredBlockCondition<T, ?>, BlockCondition<T>> {
	public static final Codec<BlockCondition<?>> CODEC = ApoliRegistries.BLOCK_CONDITION.byNameCodec();

	private final MapCodec<ConfiguredBlockCondition<T, ?>> codec;

	protected BlockCondition(MapCodec<T> codec) {
		this.codec = IConditionFactory.conditionCodec(codec, this);
	}

	@Override
	public MapCodec<ConfiguredBlockCondition<T, ?>> getConditionCodec() {
		return this.codec;
	}

	@Override
	public final ConfiguredBlockCondition<T, ?> configure(T input, ConditionData data) {
		return new ConfiguredBlockCondition<>(() -> this, input, data);
	}

	protected boolean check(T configuration, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter) {
		return false;
	}

	public boolean check(ConfiguredBlockCondition<T, ?> configuration, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> state) {
		return configuration.getData().inverted() ^ this.check(configuration.getConfiguration(), reader, position, state);
	}

	@FunctionalInterface
	public interface BlockPredicate {
		boolean test(LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> state);
	}

	@FunctionalInterface
	public interface BlockFloatFunction {
		float apply(LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> state);
	}
}
