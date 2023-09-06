package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.apace100.apoli.mixin.EntityAccessor;
import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.function.Predicate;

public class SimpleEntityCondition extends EntityCondition<NoConfiguration> {
	public static SimpleEntityCondition of(Predicate<Player> predicate) {
		return new SimpleEntityCondition(living -> living instanceof Player pe && predicate.test(pe));
	}

	public static boolean isExposedToSky(Entity entity) {
		BlockPos bp = new BlockPos((int)entity.getX(), (int) Math.round(entity.getY()), (int)entity.getZ());
		if (entity.getVehicle() instanceof Boat) bp = bp.above();
		return entity.level().canSeeSky(bp);
	}

	public static boolean isExposedToSun(Entity entity) {
		return entity.getLightLevelDependentMagicValue() > 0.5F && entity.level().isDay() && !((EntityAccessor) entity).callIsBeingRainedOn() && isExposedToSky(entity);
	}

	private final Predicate<Entity> predicate;

	public SimpleEntityCondition(Predicate<Entity> predicate) {
		super(NoConfiguration.CODEC);
		this.predicate = predicate;
	}

	@Override
	public boolean check(NoConfiguration configuration, Entity entity) {
		return this.predicate.test(entity);
	}
}
