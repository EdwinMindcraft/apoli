package dev.experimental.apoli.api.power.configuration;

import com.mojang.serialization.Codec;
import dev.experimental.apoli.api.IDynamicFeatureConfiguration;
import dev.experimental.apoli.api.power.ConditionData;
import dev.experimental.apoli.api.power.ConfiguredCondition;
import dev.experimental.apoli.api.power.factory.EntityCondition;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class ConfiguredEntityCondition<C extends IDynamicFeatureConfiguration, F extends EntityCondition<C>> extends ConfiguredCondition<C, F> {
	public static final Codec<ConfiguredEntityCondition<?, ?>> CODEC = EntityCondition.CODEC.dispatch(ConfiguredEntityCondition::getFactory, Function.identity());

	public static boolean check(@Nullable ConfiguredEntityCondition<?, ?> condition, LivingEntity entity) {
		return condition == null || condition.check(entity);
	}

	public ConfiguredEntityCondition(F factory, C configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(LivingEntity entity) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), entity);
	}
}