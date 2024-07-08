package io.github.edwinmindcraft.apoli.common.condition.bientity;

import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityCondition;
import io.github.edwinmindcraft.apoli.common.condition.meta.IDelegatedConditionConfiguration;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.tuple.Pair;

public class DelegatedBiEntityCondition<T extends IDelegatedConditionConfiguration<Pair<Entity, Entity>>> extends BiEntityCondition<T> {
	public DelegatedBiEntityCondition(MapCodec<T> codec) {
		super(codec);
	}

	@Override
	protected boolean check(T configuration, Entity user, Entity target) {
		return configuration.check(Pair.of(user, target));
	}
}
