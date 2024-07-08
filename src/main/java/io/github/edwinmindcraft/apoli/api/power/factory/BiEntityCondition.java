package io.github.edwinmindcraft.apoli.api.power.factory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.IConditionFactory;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.world.entity.Entity;

public abstract class BiEntityCondition<T extends IDynamicFeatureConfiguration> implements IConditionFactory<T, ConfiguredBiEntityCondition<T, ?>, BiEntityCondition<T>> {
	public static final Codec<BiEntityCondition<?>> CODEC = ApoliRegistries.BIENTITY_CONDITION.byNameCodec();
	private final MapCodec<ConfiguredBiEntityCondition<T, ?>> codec;

	protected BiEntityCondition(MapCodec<T> codec) {
		this.codec = IConditionFactory.conditionCodec(codec, this);
	}

	@Override
	public MapCodec<ConfiguredBiEntityCondition<T, ?>> getConditionCodec() {
		return this.codec;
	}

	@Override
	public final ConfiguredBiEntityCondition<T, ?> configure(T input, ConditionData data) {
		return new ConfiguredBiEntityCondition<>(() -> this, input, data);
	}

	protected abstract boolean check(T configuration, Entity actor, Entity target);

	public boolean check(T configuration, ConditionData data, Entity actor, Entity target) {
		return data.inverted() ^ this.check(configuration, actor, target);
	}
}
