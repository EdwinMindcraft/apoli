package io.github.edwinmindcraft.apoli.api.power.factory;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.IConditionFactory;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredDamageCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.world.damagesource.DamageSource;

public abstract class DamageCondition<T extends IDynamicFeatureConfiguration> implements IConditionFactory<T, ConfiguredDamageCondition<T, ?>, DamageCondition<T>> {
	public static final Codec<DamageCondition<?>> CODEC = ApoliRegistries.DAMAGE_CONDITION.byNameCodec();

	private final Codec<ConfiguredDamageCondition<T, ?>> codec;

	protected DamageCondition(Codec<T> codec) {
		this.codec = IConditionFactory.conditionCodec(codec, this);
	}

	@Override
	public Codec<ConfiguredDamageCondition<T, ?>> getConditionCodec() {
		return this.codec;
	}

	@Override
	public final ConfiguredDamageCondition<T, ?> configure(T input, ConditionData data) {
		return new ConfiguredDamageCondition<>(() -> this, input, data);
	}

	protected boolean check(T configuration, DamageSource source, float amount) {
		return false;
	}

	public boolean check(T configuration, ConditionData data, DamageSource source, float amount) {
		return data.inverted() ^ this.check(configuration, source, amount);
	}
}
