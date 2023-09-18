package io.github.edwinmindcraft.apoli.common.action.bientity;

import io.github.apace100.apoli.util.Space;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.AddBiEntityVelocityConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class AddVelocityAction extends BiEntityAction<AddBiEntityVelocityConfiguration> {
	public AddVelocityAction() {
		super(AddBiEntityVelocityConfiguration.CODEC);
	}

	@Override
	public void execute(AddBiEntityVelocityConfiguration configuration, Entity actor, Entity target) {
		if (target instanceof Player && (target.level().isClientSide() ? !configuration.client() : !configuration.server()))
			return;
		Vector3f vec = new Vector3f(configuration.direction());
        Consumer<Vec3> method = configuration.set() ? target::setDeltaMovement : target::addDeltaMovement;
        Space.transformVectorToBase(target.position().subtract(actor.position()), vec, actor.getYRot(), true); // vector normalized by method
        method.accept(new Vec3(vec));
		target.hurtMarked = true;
	}
}
