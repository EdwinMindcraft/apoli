package io.github.edwinmindcraft.apoli.api.power.factory;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.IFactory;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public abstract class BlockAction<T extends IDynamicFeatureConfiguration> implements IFactory<T, ConfiguredBlockAction<T, ?>, BlockAction<T>> {
	public static final Codec<BlockAction<?>> CODEC = ApoliRegistries.codec(() -> ApoliRegistries.BLOCK_ACTION.get());

	private final Codec<ConfiguredBlockAction<T, ?>> codec;

	protected BlockAction(Codec<T> codec) {
		this.codec = IFactory.singleCodec(IFactory.asMap(codec), this::configure, ConfiguredBlockAction::getConfiguration);
	}

	public Codec<ConfiguredBlockAction<T, ?>> getCodec() {
		return this.codec;
	}

	@Override
	public final ConfiguredBlockAction<T, ?> configure(T input) {
		return new ConfiguredBlockAction<>(() -> this, input);
	}

	public abstract void execute(T configuration, Level world, BlockPos pos, Direction direction);
}
