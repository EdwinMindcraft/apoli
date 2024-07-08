package io.github.edwinmindcraft.apoli.common.condition.damage;

import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.apoli.api.power.factory.DamageCondition;
import io.github.edwinmindcraft.apoli.common.condition.meta.IDelegatedConditionConfiguration;
import net.minecraft.world.damagesource.DamageSource;
import org.apache.commons.lang3.tuple.Pair;

public class DelegatedDamageCondition<T extends IDelegatedConditionConfiguration<Pair<DamageSource, Float>>> extends DamageCondition<T> {
	public DelegatedDamageCondition(MapCodec<T> codec) {
		super(codec);
	}

	@Override
	protected boolean check(T configuration, DamageSource source, float amount) {
		return configuration.check(Pair.of(source, amount));
	}
}
