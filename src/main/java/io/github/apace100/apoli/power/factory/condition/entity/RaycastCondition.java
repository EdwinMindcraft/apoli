package io.github.apace100.apoli.power.factory.condition.entity;

import io.github.apace100.apoli.condition.configuration.RaycastConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class RaycastCondition extends EntityCondition<RaycastConfiguration> {

	public RaycastCondition() {
		super(RaycastConfiguration.CODEC);
	}

	@Override
	protected boolean check(@NotNull RaycastConfiguration configuration, @NotNull Entity entity) {
		HitResult hitResult = configuration.settings().perform(entity, configuration.matchCondition());

		if (hitResult.getType() == HitResult.Type.MISS)
			return false;
		if (hitResult instanceof BlockHitResult bhr && configuration.blockCondition().isBound())
			return ConfiguredBlockCondition.check(configuration.blockCondition(), entity.level(), bhr.getBlockPos());
		if (hitResult instanceof EntityHitResult ehr && configuration.hitCondition().isBound())
			return configuration.hitCondition().value().check(entity, ehr.getEntity());
		return true;
	}

	/*public static ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(Apoli.identifier("raycast"),
				new SerializableData()
						.add("distance", SerializableDataTypes.DOUBLE)
						.add("block", SerializableDataTypes.BOOLEAN, true)
						.add("entity", SerializableDataTypes.BOOLEAN, true)
						.add("shape_type", SerializableDataType.enumValue(RaycastContext.ShapeType.class), RaycastContext.ShapeType.OUTLINE)
						.add("fluid_handling", SerializableDataType.enumValue(RaycastContext.FluidHandling.class), RaycastContext.FluidHandling.ANY)
						.add("match_bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
						.add("hit_bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
						.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null),
				RaycastCondition::condition
		);
	}*/
}
