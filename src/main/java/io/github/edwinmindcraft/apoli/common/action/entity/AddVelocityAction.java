package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.AddVelocityConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class AddVelocityAction extends EntityAction<AddVelocityConfiguration> {
	public AddVelocityAction() {
		super(AddVelocityConfiguration.CODEC);
	}

	@Override
	public void execute(AddVelocityConfiguration configuration, Entity entity) {
		if (entity instanceof Player && (entity.level().isClientSide() ? !configuration.client() : !configuration.server()))
			return;
        // Because we are modifying the vector passed in by using toGlobal, we have to make a new vector which is a copy of it.
		Vector3f vec = new Vector3f(configuration.direction());
		Consumer<Vec3> method = configuration.set() ? entity::setDeltaMovement : entity::addDeltaMovement;
		configuration.space().toGlobal(vec, entity);
		method.accept(new Vec3(vec));
		entity.hurtMarked = true;
	}
}
