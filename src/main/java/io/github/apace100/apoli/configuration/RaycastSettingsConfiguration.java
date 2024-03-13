package io.github.apace100.apoli.configuration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;


public record RaycastSettingsConfiguration(double distance, boolean block, boolean entity, ClipContext.Block shapeType,
										   ClipContext.Fluid fluidHandling) implements IDynamicFeatureConfiguration {
	public static final MapCodec<RaycastSettingsConfiguration> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			CalioCodecHelper.DOUBLE.fieldOf("distance").forGetter(RaycastSettingsConfiguration::distance),
			CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "block", true).forGetter(RaycastSettingsConfiguration::block),
			CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "entity", true).forGetter(RaycastSettingsConfiguration::entity),
			CalioCodecHelper.optionalField(SerializableDataTypes.SHAPE_TYPE, "shape_type", ClipContext.Block.OUTLINE).forGetter(RaycastSettingsConfiguration::shapeType),
			CalioCodecHelper.optionalField(SerializableDataTypes.FLUID_HANDLING, "fluid_handling", ClipContext.Fluid.ANY).forGetter(RaycastSettingsConfiguration::fluidHandling)
	).apply(instance, RaycastSettingsConfiguration::new));

	@Override
	public boolean isConfigurationValid() {
		return this.block() || this.entity();
	}

	@NotNull
	public HitResult perform(@NotNull Entity entity, Holder<ConfiguredBiEntityCondition<?, ?>> entityValidator) {
		return this.perform(entity, new Vec3(entity.getX(), entity.getEyeY(), entity.getZ()), entity.getViewVector(1), entityValidator);
	}

	@NotNull
	public HitResult perform(@NotNull Entity entity, @NotNull Vec3 origin, @NotNull Vec3 direction, Holder<ConfiguredBiEntityCondition<?, ?>> entityValidator) {
		Vec3 target = origin.add(direction.normalize().scale(this.distance()));
		HitResult result = null;
		if (this.entity())
			result = this.performEntityRaycast(entity, origin, target, entityValidator);
		if (this.block()) {
			BlockHitResult blockHit = this.performBlockRaycast(entity, origin, target);
			if (blockHit.getType() != HitResult.Type.MISS) {
				if (result == null || result.getType() == HitResult.Type.MISS || result.distanceTo(entity) > blockHit.distanceTo(entity))
					result = blockHit;
			}
		}
		if (result == null)
			return BlockHitResult.miss(origin, Direction.getNearest(direction.x, direction.y, direction.z), new BlockPos(target));
		return result;
	}

	private BlockHitResult performBlockRaycast(Entity source, Vec3 origin, Vec3 target) {
		ClipContext context = new ClipContext(origin, target, this.shapeType(), this.fluidHandling(), source);
		return source.level.clip(context);
	}

	private EntityHitResult performEntityRaycast(Entity source, Vec3 origin, Vec3 target, Holder<ConfiguredBiEntityCondition<?, ?>> biEntityCondition) {
		Vec3 ray = target.subtract(origin);
		AABB box = source.getBoundingBox().expandTowards(ray).inflate(1.0D, 1.0D, 1.0D);
		return ProjectileUtil.getEntityHitResult(source, origin, target, box, (entityx) -> !entityx.isSpectator() && ConfiguredBiEntityCondition.check(biEntityCondition, source, entityx), ray.lengthSqr());
	}
}
