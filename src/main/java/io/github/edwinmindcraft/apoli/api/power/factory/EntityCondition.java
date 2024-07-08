package io.github.edwinmindcraft.apoli.api.power.factory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.IConditionFactory;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.world.entity.Entity;

public abstract class EntityCondition<T extends IDynamicFeatureConfiguration> implements IConditionFactory<T, ConfiguredEntityCondition<T, ?>, EntityCondition<T>> {
	public static final Codec<EntityCondition<?>> CODEC = ApoliRegistries.ENTITY_CONDITION.byNameCodec();
	private final MapCodec<ConfiguredEntityCondition<T, ?>> codec;

	protected EntityCondition(MapCodec<T> codec) {
		this.codec = IConditionFactory.conditionCodec(codec, this);
	}

	@Override
	public MapCodec<ConfiguredEntityCondition<T, ?>> getConditionCodec() {
		return this.codec;
	}

	@Override
	public final ConfiguredEntityCondition<T, ?> configure(T input, ConditionData data) {
		return new ConfiguredEntityCondition<>(() -> this, input, data);
	}

	protected boolean check(T configuration, Entity entity) {
		return false;
	}

	public boolean check(T configuration, ConditionData data, Entity entity) {
		return data.inverted() ^ this.check(configuration, entity);
	}
}
