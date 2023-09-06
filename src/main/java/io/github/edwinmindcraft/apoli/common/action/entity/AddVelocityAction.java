package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.AddVelocityConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.util.TriConsumer;
import org.joml.Vector3f;

public class AddVelocityAction extends EntityAction<AddVelocityConfiguration> {
	public AddVelocityAction() {
		super(AddVelocityConfiguration.CODEC);
	}

	@Override
	public void execute(AddVelocityConfiguration configuration, Entity entity) {
		if (entity instanceof Player && (entity.level().isClientSide() ? !configuration.client() : !configuration.server()))
			return;
		Vector3f vec = configuration.direction();
		TriConsumer<Float, Float, Float> method = configuration.set() ? entity::setDeltaMovement : entity::push;
		configuration.space().toGlobal(vec, entity);
		method.accept(vec.x(), vec.y(), vec.z());
		entity.hurtMarked = true;
	}
}
