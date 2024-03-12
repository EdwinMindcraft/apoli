package io.github.edwinmindcraft.apoli.api.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConfiguredFactory;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public final class ConfiguredBlockAction<C extends IDynamicFeatureConfiguration, F extends BlockAction<C>> extends ConfiguredFactory<C, F, ConfiguredBlockAction<?, ?>> {
	public static final Codec<ConfiguredBlockAction<?, ?>> CODEC = BlockAction.CODEC.dispatch(ConfiguredBlockAction::getFactory, BlockAction::getCodec);
	public static final CodecSet<ConfiguredBlockAction<?, ?>> CODEC_SET = CalioCodecHelper.forDynamicRegistry(ApoliDynamicRegistries.CONFIGURED_BLOCK_ACTION_KEY, SerializableDataTypes.IDENTIFIER, CODEC);
	public static final Codec<Holder<ConfiguredBlockAction<?, ?>>> HOLDER = CODEC_SET.holder();

	public static MapCodec<Holder<ConfiguredBlockAction<?, ?>>> required(String name) {
		return HOLDER.fieldOf(name);
	}

	public static MapCodec<Holder<ConfiguredBlockAction<?, ?>>> optional(String name) {
		return CalioCodecHelper.registryDefaultedField(HOLDER, name, ApoliDynamicRegistries.CONFIGURED_BLOCK_ACTION_KEY, ApoliBuiltinRegistries.CONFIGURED_BLOCK_ACTIONS);
	}

	public static void execute(Holder<ConfiguredBlockAction<?, ?>> blockAction, Level world, BlockPos pos, Direction direction) {
		if (blockAction.isBound())
			blockAction.value().execute(world, pos, direction);
	}


	public ConfiguredBlockAction(Supplier<F> factory, C configuration) {
		super(factory, configuration);
	}

	public void execute(Level world, BlockPos pos, Direction direction) {
		this.getFactory().execute(this.getConfiguration(), world, pos, direction);
	}


	@Override
	public String toString() {
		return "CBA:" + ApoliRegistries.BLOCK_ACTION.getKey(this.getFactory()) + "-" + this.getConfiguration();
	}
}
