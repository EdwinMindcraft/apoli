package io.github.edwinmindcraft.apoli.api.power.configuration.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

public interface TogglePowerConfiguration extends IDynamicFeatureConfiguration {
	MapCodec<TogglePowerConfiguration> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "active_by_default", true).forGetter(TogglePowerConfiguration::defaultState),
			ExtraCodecs.strictOptionalField(IActivePower.Key.BACKWARD_COMPATIBLE_CODEC, "key", IActivePower.Key.PRIMARY).forGetter(TogglePowerConfiguration::key),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "retain_state", true).forGetter(TogglePowerConfiguration::retainState)
	).apply(instance, Impl::new));

	MapCodec<TogglePowerConfiguration> INACTIVE_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "active_by_default", false).forGetter(TogglePowerConfiguration::defaultState),
			ExtraCodecs.strictOptionalField(IActivePower.Key.BACKWARD_COMPATIBLE_CODEC, "key", IActivePower.Key.PRIMARY).forGetter(TogglePowerConfiguration::key),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "retain_state", true).forGetter(TogglePowerConfiguration::retainState)
	).apply(instance, Impl::new));

	Codec<TogglePowerConfiguration> CODEC = MAP_CODEC.codec();
	Codec<TogglePowerConfiguration> INACTIVE_CODEC = INACTIVE_MAP_CODEC.codec();

	boolean defaultState();

	IActivePower.Key key();

	boolean retainState();

	record Impl(boolean defaultState, IActivePower.Key key, boolean retainState) implements TogglePowerConfiguration {
	}

	interface Wrapper extends TogglePowerConfiguration {
		TogglePowerConfiguration wrapped();

		@Override
		default boolean defaultState() {
			return this.wrapped().defaultState();
		}

		@Override
		default IActivePower.Key key() {
			return this.wrapped().key();
		}

		@Override
		default boolean retainState() {
			return this.wrapped().retainState();
		}
	}
}
