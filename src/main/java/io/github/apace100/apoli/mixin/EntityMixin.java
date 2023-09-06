package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.access.MovingEntity;
import io.github.apace100.apoli.access.SubmergableEntity;
import io.github.apace100.apoli.access.WaterMovingEntity;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.common.power.ModifyVelocityPower;
import io.github.edwinmindcraft.apoli.common.power.PhasingPower;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin implements MovingEntity, SubmergableEntity {

	@Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
	private void makeFullyFireImmune(CallbackInfoReturnable<Boolean> cir) {
		if (IPowerContainer.hasPower((Entity) (Object) this, ApoliPowers.FIRE_IMMUNITY.get())) {
			cir.setReturnValue(true);
		}
	}

	@Shadow
	public Level level;
	@Shadow
	public float moveDist;
	@Shadow
	protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;
	private boolean isMoving;
	private float distanceBefore;
	@Shadow
	protected boolean onGround;
	@Final
	@Shadow
	private Set<TagKey<Fluid>> fluidOnEyes;

	@Shadow
	public abstract double getFluidHeight(TagKey<Fluid> fluid);

	@Inject(method = "isInWater", at = @At("HEAD"), cancellable = true)
	private void makeEntitiesIgnoreWater(CallbackInfoReturnable<Boolean> cir) {
		if (IPowerContainer.hasPower((Entity) (Object) this, ApoliPowers.IGNORE_WATER.get())) {
			if (this instanceof WaterMovingEntity) {
				if (((WaterMovingEntity) this).isInMovementPhase()) {
					cir.setReturnValue(false);
				}
			}
		}
	}

	@Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isInWaterRainOrBubble()Z"))
	private boolean preventExtinguishingFromSwimming(Entity entity) {
		if (entity.isSwimming() && this.getFluidHeight(FluidTags.WATER) <= 0 && IPowerContainer.hasPower(entity, ApoliPowers.SWIMMING.get()))
			return false;
		return entity.isInWaterRainOrBubble();
	}

    /*@Inject(method = "fall", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onLandedUpon(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;F)V"))
    private void invokeActionOnLand(CallbackInfo ci) {
        List<ActionOnLandPower> powers = PowerHolderComponent.getPowers((Entity)(Object)this, ActionOnLandPower.class);
        powers.forEach(ActionOnLandPower::executeAction);
    }*/

   /* @Inject(at = @At("HEAD"), method = "isInvulnerableTo", cancellable = true)
    private void makeOriginInvulnerable(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if((Object)this instanceof LivingEntity) {
            PowerHolderComponent component = PowerHolderComponent.KEY.get(this);
            if(component.getPowers(InvulnerablePower.class).stream().anyMatch(inv -> inv.doesApply(damageSource))) {
                cir.setReturnValue(true);
            }
        }
    }*/

	@Inject(at = @At("HEAD"), method = "isInvisible", cancellable = true)
	private void phantomInvisibility(CallbackInfoReturnable<Boolean> info) {
		if (IPowerContainer.hasPower((Entity) (Object) this, ApoliPowers.INVISIBILITY.get()))
			info.setReturnValue(true);
	}

	@Inject(at = @At(value = "HEAD"), method = "moveTowardsClosestSpace", cancellable = true)
	protected void pushOutOfBlocks(double x, double y, double z, CallbackInfo info) {
		if (PhasingPower.shouldPhaseThrough((Entity) (Object) this, new BlockPos((int) x, (int) y, (int) z)))
			info.cancel();
	}

	/*
	Apparently targeting lambda doesn't work. Who knew.
	@Redirect(method = "lambda$isInWall$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
	private VoxelShape preventPhasingSuffocation(BlockState state, BlockGetter world, BlockPos pos) {
		return state.getCollisionShape(world, pos, CollisionContext.of((Entity) (Object) this));
	}*/

	@Inject(method = "move", at = @At("HEAD"))
	private void saveDistanceTraveled(MoverType type, Vec3 movement, CallbackInfo ci) {
		this.isMoving = false;
		this.distanceBefore = this.moveDist;
	}

	@Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
	private void checkIsMoving(MoverType type, Vec3 movement, CallbackInfo ci) {
		if (this.moveDist > this.distanceBefore)
			this.isMoving = true;
	}

	@ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true)
	private Vec3 modifyMovementVelocity(Vec3 original, MoverType movementType) {
		if(!IPowerContainer.hasPower((Entity)(Object)this, ApoliPowers.MODIFY_VELOCITY.get()) || movementType != MoverType.SELF) {
			return original;
		}
		return ModifyVelocityPower.getModifiedVelocity((Entity)(Object)this, original);
	}

	@Override
	public boolean isSubmergedInLoosely(TagKey<Fluid> tag) {
		if (tag == null || this.fluidOnEyes == null) {
			return false;
		}
		return this.fluidOnEyes.contains(tag);
		//return Calio.areTagsEqual(Registry.FLUID_KEY, tag, submergedFluidTag);
	}

	@Override
	public double getFluidHeightLoosely(TagKey<Fluid> tag) {
		if (tag == null) {
			return 0;
		}
		if (this.fluidHeight.containsKey(tag)) {
			return this.fluidHeight.getDouble(tag);
		}
		for (TagKey<Fluid> ft : this.fluidHeight.keySet()) {
			if (ft.equals(tag)) {
				return this.fluidHeight.getDouble(ft);
			}
		}
		return 0;
	}

	@Override
	public boolean isMoving() {
		return this.isMoving;
	}
}
