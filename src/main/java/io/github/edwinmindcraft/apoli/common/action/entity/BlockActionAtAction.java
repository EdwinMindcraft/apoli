package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.edwinmindcraft.apoli.api.configuration.HolderConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;

public class BlockActionAtAction extends EntityAction<HolderConfiguration<ConfiguredBlockAction<?, ?>>> {

	public BlockActionAtAction() {
		super(HolderConfiguration.required(ConfiguredBlockAction.required("block_action")));
	}

	@Override
	public void execute(HolderConfiguration<ConfiguredBlockAction<?, ?>> configuration, Entity entity) {
		ConfiguredBlockAction.execute(configuration.holder(), entity.level(), entity.blockPosition(), Direction.UP);
	}
}
