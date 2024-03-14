package io.github.edwinmindcraft.apoli.api.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.ConfiguredCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Represents a block condition.
 *
 * @param <T> The configuration of this condition
 * @param <F> The executor of this condition.
 * @author Edwin
 */
public final class ConfiguredBlockCondition<T extends IDynamicFeatureConfiguration, F extends BlockCondition<T>> extends ConfiguredCondition<T, F, ConfiguredBlockCondition<?, ?>> {
	public static final Codec<ConfiguredBlockCondition<?, ?>> CODEC = BlockCondition.CODEC.dispatch(ConfiguredBlockCondition::getFactory, BlockCondition::getConditionCodec);
	public static final MapCodec<Optional<ConfiguredBlockCondition<?, ?>>> OPTIONAL_FIELD = ExtraCodecs.strictOptionalField(CODEC, "block_condition");
	public static final CodecSet<ConfiguredBlockCondition<?, ?>> CODEC_SET = CalioCodecHelper.forDynamicRegistry(ApoliDynamicRegistries.CONFIGURED_BLOCK_CONDITION_KEY, SerializableDataTypes.IDENTIFIER, CODEC);
	public static final Codec<Holder<ConfiguredBlockCondition<?, ?>>> HOLDER = CODEC_SET.holder();

	public static MapCodec<Holder<ConfiguredBlockCondition<?, ?>>> required(String name) {
		return HOLDER.fieldOf(name);
	}

	public static MapCodec<Holder<ConfiguredBlockCondition<?, ?>>> optional(String name) {
		return CalioCodecHelper.registryDefaultedField(HOLDER, name, ApoliDynamicRegistries.CONFIGURED_BLOCK_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_BLOCK_CONDITIONS);
	}

	public static MapCodec<Holder<ConfiguredBlockCondition<?, ?>>> optional(String name, ResourceKey<ConfiguredBlockCondition<?, ?>> key) {
		return CalioCodecHelper.registryField(HOLDER, name, key, ApoliDynamicRegistries.CONFIGURED_BLOCK_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_BLOCK_CONDITIONS);
	}

	public static MapCodec<Holder<ConfiguredBlockCondition<?, ?>>> optional(String name, ResourceLocation key) {
		return CalioCodecHelper.registryField(HOLDER, name, ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_BLOCK_CONDITION_KEY, key), ApoliDynamicRegistries.CONFIGURED_BLOCK_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_BLOCK_CONDITIONS);
	}

	/**
	 * Checks if the block condition is valid at a given point.
	 *
	 * @param condition The condition to check for
	 * @param position  The {@link BlockPos position} to check the condition at
	 * @param state     The fast access to the {@link BlockState} to check the condition for
	 * @return The result of the check or {@code true} if the condition is {@code null}
	 * @see #check(LevelReader, BlockPos, Supplier) for a non-nullable version
	 * @see #check(Holder, LevelReader, BlockPos) for a version that accesses the state from the world
	 */
	public static boolean check(Holder<ConfiguredBlockCondition<?, ?>> condition, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> state) {
		return !condition.isBound() || condition.value().check(reader, position, state);
	}

	/**
	 * Checks if the block condition is valid at a given point.
	 *
	 * @param condition The condition to check for
	 * @param position  The {@link BlockPos position} to check the condition at
	 * @return The result of the check or {@code true} if the condition is {@code null}
	 * @see #check(Holder, LevelReader, BlockPos, Supplier) for a version with a block state provider
	 */
	public static boolean check(Holder<ConfiguredBlockCondition<?, ?>> condition, LevelReader reader, BlockPos position) {
		return check(condition, reader, position, () -> reader.getBlockState(position));
	}

	public ConfiguredBlockCondition(Supplier<F> factory, T configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	/**
	 * Checks if the block condition is valid at a given point.
	 *
	 * @param reader   The {@link LevelReader level} to check the condition in
	 * @param position The {@link BlockPos position} to check the condition at
	 * @param state    The fast access to the {@link BlockState} to check the condition for
	 * @return The result of the check
	 * @see #check(Holder, LevelReader, BlockPos, Supplier)  for a nullable version
	 */
	public boolean check(LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> state) {
		return this.getFactory().check(this, reader, position, state);
	}


	@Override
	public String toString() {
		return "CBC:" + ApoliRegistries.BLOCK_CONDITION.getKey(this.getFactory()) + "(" + this.getData() + ")-" + this.getConfiguration();
	}
}