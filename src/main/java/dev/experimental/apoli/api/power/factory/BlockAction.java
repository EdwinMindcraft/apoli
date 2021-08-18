package dev.experimental.apoli.api.power.factory;

import com.mojang.serialization.Codec;
import dev.experimental.apoli.api.IDynamicFeatureConfiguration;
import dev.experimental.apoli.api.power.IFactory;
import dev.experimental.apoli.api.power.configuration.ConfiguredBlockAction;
import dev.experimental.apoli.api.registry.ApoliRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class BlockAction<T extends IDynamicFeatureConfiguration> extends ForgeRegistryEntry<BlockAction<?>> implements IFactory<T, ConfiguredBlockAction<T, ?>, BlockAction<T>> {
	public static final Codec<BlockAction<?>> CODEC = ApoliRegistries.codec(ApoliRegistries.BLOCK_ACTION);

	private final Codec<T> codec;

	protected BlockAction(Codec<T> codec) {
		this.codec = codec;
	}

	@Override
	public Codec<T> getCodec() {
		return codec;
	}

	@Override
	public final ConfiguredBlockAction<T, ?> configure(T input) {
		return new ConfiguredBlockAction<>(this, input);
	}

	public abstract void execute(T configuration, Level world, BlockPos pos, Direction direction);
}
