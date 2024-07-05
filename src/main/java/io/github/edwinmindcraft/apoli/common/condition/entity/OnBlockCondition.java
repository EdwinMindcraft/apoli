package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.edwinmindcraft.apoli.api.configuration.HolderConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public class OnBlockCondition extends EntityCondition<HolderConfiguration<ConfiguredBlockCondition<?, ?>>> {

	public OnBlockCondition() {
		super(HolderConfiguration.optional(ConfiguredBlockCondition.optional("block_condition")));
	}

	@Override
	public boolean check(HolderConfiguration<ConfiguredBlockCondition<?, ?>> configuration, Entity entity) {
		return entity.onGround() && ConfiguredBlockCondition.check(configuration.holder(), entity.level(), entity.getOnPos());
	}
}
