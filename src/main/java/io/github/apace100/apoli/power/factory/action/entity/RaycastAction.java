package io.github.apace100.apoli.power.factory.action.entity;

import io.github.apace100.apoli.action.configuration.RaycastConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.CommandConfiguration;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;

public class RaycastAction extends EntityAction<RaycastConfiguration> {

	public RaycastAction() {
		super(RaycastConfiguration.CODEC);
	}

	public void execute(@NotNull RaycastConfiguration configuration, @NotNull Entity entity) {
		ConfiguredEntityAction.execute(configuration.beforeAction(), entity);
		Vec3 direction = entity.getViewVector(1);
		Vec3 origin = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
		HitResult hitResult = configuration.settings().perform(entity, origin, direction, configuration.biEntityCondition());
		RaycastConfiguration.CommandInfo commandInfo = configuration.commandInfo();
		RaycastConfiguration.HitAction actions = configuration.action();
		if (hitResult.getType() != HitResult.Type.MISS) {
			if (commandInfo.commandAtHit() != null) {
				Vec3 offsetDirection = direction;
				double offset = 0;
				Vec3 hitPos = hitResult.getLocation();
				if (commandInfo.commandHitOffset() != null) {
					offset = commandInfo.commandHitOffset();
				} else {
					if (hitResult instanceof BlockHitResult bhr) {
						if (bhr.getDirection() == Direction.DOWN) {
							offset = entity.getBbHeight();
						} else if (bhr.getDirection() == Direction.UP) {
							offset = 0;
						} else {
							offset = entity.getBbWidth() / 2;
							offsetDirection = new Vec3(
									-bhr.getDirection().getStepX(),
									-bhr.getDirection().getStepY(),
									-bhr.getDirection().getStepZ()
							).reverse();
						}
					}
					offset += 0.05;
				}
				Vec3 at = hitPos.subtract(offsetDirection.scale(offset));
				executeCommandAtHit(entity, at, commandInfo.commandAtHit());
			}
			if (commandInfo.commandAlongRay() != null) {
				executeStepCommands(entity, origin, hitResult.getLocation(), commandInfo.commandAlongRay(), commandInfo.commandStep());
			}
			if (hitResult instanceof BlockHitResult bhr)
				ConfiguredBlockAction.execute(actions.blockAction(), entity.level(), bhr.getBlockPos(), bhr.getDirection());
			if (hitResult instanceof EntityHitResult ehr)
				ConfiguredBiEntityAction.execute(actions.biEntityAction(), entity, ehr.getEntity());
			ConfiguredEntityAction.execute(actions.hitAction(), entity);
		} else {
			if (commandInfo.commandAlongRay() != null && !commandInfo.commandAlongRayOnlyOnHit())
				executeStepCommands(entity, origin, hitResult.getLocation(), commandInfo.commandAlongRay(), commandInfo.commandStep());
			ConfiguredEntityAction.execute(actions.missAction(), entity);
		}
	}

	private static void executeStepCommands(Entity entity, Vec3 origin, Vec3 target, String command, double step) {
		Vec3 direction = target.subtract(origin).normalize();
		double length = origin.distanceTo(target);
		for (double current = 0; current < length; current += step) {
			CommandConfiguration.executeAt(entity, origin.add(direction.scale(current)), command);
		}
	}

	private static void executeCommandAtHit(Entity entity, Vec3 hitPosition, String command) {
		CommandConfiguration.executeAt(entity, hitPosition, command);
	}

	private static BlockHitResult performBlockRaycast(Entity source, Vec3 origin, Vec3 target, ClipContext.Block shapeType, ClipContext.Fluid fluidHandling) {
		ClipContext context = new ClipContext(origin, target, shapeType, fluidHandling, source);
		return source.level().clip(context);
	}

	private static EntityHitResult performEntityRaycast(Entity source, Vec3 origin, Vec3 target, Holder<ConfiguredBiEntityCondition<?, ?>> biEntityCondition) {
		Vec3 ray = target.subtract(origin);
		AABB box = source.getBoundingBox().expandTowards(ray).inflate(1.0D, 1.0D, 1.0D);
		return ProjectileUtil.getEntityHitResult(source, origin, target, box, (entityx) -> !entityx.isSpectator() && ConfiguredBiEntityCondition.check(biEntityCondition, source, entityx), ray.lengthSqr());
	}
/*
	public static ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(Apoli.identifier("raycast"),
				new SerializableData()
						.add("distance", SerializableDataTypes.DOUBLE)
						.add("block", SerializableDataTypes.BOOLEAN, true)
						.add("entity", SerializableDataTypes.BOOLEAN, true)
						.add("shape_type", SerializableDataType.enumValue(RaycastContext.ShapeType.class), RaycastContext.ShapeType.OUTLINE)
						.add("fluid_handling", SerializableDataType.enumValue(RaycastContext.FluidHandling.class), RaycastContext.FluidHandling.ANY)
						.add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
						.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
						.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
						.add("command_at_hit", SerializableDataTypes.STRING, null)
						.add("command_hit_offset", SerializableDataTypes.DOUBLE, null)
						.add("command_along_ray", SerializableDataTypes.STRING, null)
						.add("command_step", SerializableDataTypes.DOUBLE, 1.0)
						.add("command_along_ray_only_on_hit", SerializableDataTypes.BOOLEAN, false)
						.add("before_action", ApoliDataTypes.ENTITY_ACTION, null)
						.add("hit_action", ApoliDataTypes.ENTITY_ACTION, null)
						.add("miss_action", ApoliDataTypes.ENTITY_ACTION, null),
				RaycastAction::action
		);
	}

 */
}
