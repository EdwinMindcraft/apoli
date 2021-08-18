package dev.experimental.apoli.api.power.factory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.experimental.apoli.api.IDynamicFeatureConfiguration;
import dev.experimental.apoli.api.power.ConditionData;
import dev.experimental.apoli.api.power.IConditionFactory;
import dev.experimental.apoli.api.power.configuration.ConfiguredDamageCondition;
import dev.experimental.apoli.api.registry.ApoliRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class DamageCondition<T extends IDynamicFeatureConfiguration> extends ForgeRegistryEntry<DamageCondition<?>> implements IConditionFactory<T, ConfiguredDamageCondition<T, ?>, DamageCondition<T>> {
	public static final Codec<DamageCondition<?>> CODEC = ApoliRegistries.codec(ApoliRegistries.DAMAGE_CONDITION);

	private final Codec<Pair<T, ConditionData>> codec;

	protected DamageCondition(Codec<T> codec) {
		this.codec = IConditionFactory.conditionCodec(codec);
	}

	@Override
	public Codec<Pair<T, ConditionData>> getConditionCodec() {
		return this.codec;
	}

	@Override
	public final ConfiguredDamageCondition<T, ?> configure(T input, ConditionData data) {
		return new ConfiguredDamageCondition<>(this, input, data);
	}

	protected boolean check(T configuration, DamageSource source, float amount) {
		return false;
	}

	public boolean check(T configuration, ConditionData data, DamageSource source, float amount) {
		return data.inverted() ^ this.check(configuration, data, source, amount);
	}
}
