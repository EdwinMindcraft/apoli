package io.github.edwinmindcraft.apoli.common.condition.bientity;

import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.ClipContextConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class LineOfSightCondition extends BiEntityCondition<ClipContextConfiguration> {
	public LineOfSightCondition() {
		super(ClipContextConfiguration.CODEC);
	}

	@Override
	protected boolean check(ClipContextConfiguration configuration, Entity actor, Entity target) {
		if (!Objects.equals(actor.level(), target.level())) {
			return false;
		} else {
			Vec3 vec3d = new Vec3(actor.getX(), actor.getEyeY(), actor.getZ());
			Vec3 vec3d2 = new Vec3(target.getX(), target.getEyeY(), target.getZ());
			if (vec3d2.distanceTo(vec3d) > 128.0D) {
				return false;
			} else {
				return actor.level().clip(new ClipContext(vec3d, vec3d2, configuration.block(), configuration.fluid(), actor)).getType() == HitResult.Type.MISS;
			}
		}
	}
}
