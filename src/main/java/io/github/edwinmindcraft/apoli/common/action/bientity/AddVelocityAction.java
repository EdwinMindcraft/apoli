package io.github.edwinmindcraft.apoli.common.action.bientity;

import io.github.apace100.apoli.util.Space;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.AddBiEntityVelocityConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.util.TriConsumer;
import org.joml.Vector3f;

public class AddVelocityAction extends BiEntityAction<AddBiEntityVelocityConfiguration> {
	public AddVelocityAction() {
		super(AddBiEntityVelocityConfiguration.CODEC);
	}

	@Override
	public void execute(AddBiEntityVelocityConfiguration configuration, Entity actor, Entity target) {
		if (target instanceof Player && (target.level().isClientSide() ? !configuration.client() : !configuration.server()))
			return;
		Vector3f vec = configuration.direction();
		TriConsumer<Float, Float, Float> method = configuration.set() ? target::setDeltaMovement : target::push;
		Space.transformVectorToBase(target.position().subtract(actor.position()), vec, actor.getYRot(), true); // vector normalized by method
		method.accept(vec.x(), vec.y(), vec.z());
		target.hurtMarked = true;
	}
}
