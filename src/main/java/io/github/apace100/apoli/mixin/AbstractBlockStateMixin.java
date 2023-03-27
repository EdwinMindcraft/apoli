package io.github.apace100.apoli.mixin;

import io.github.edwinmindcraft.apoli.common.power.PhasingPower;
import io.github.edwinmindcraft.apoli.common.power.PreventBlockActionPower;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class AbstractBlockStateMixin {

	@Shadow
	public abstract Block getBlock();

	@Shadow
	protected abstract @NotNull BlockState asState();

	@Inject(at = @At("RETURN"), method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
	private void phaseThroughBlocks(BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> info) {
		VoxelShape blockShape = info.getReturnValue();
		if (!blockShape.isEmpty() && context instanceof EntityCollisionContext esc) {
			if (esc.getEntity() != null) {
				Entity entity = esc.getEntity();
				boolean isAbove = this.isAbove(entity, blockShape, pos, false);
				if (world instanceof LevelReader reader && entity instanceof LivingEntity living && PhasingPower.shouldPhaseThrough(living, reader, pos, this::asState, isAbove))
					info.setReturnValue(Shapes.empty());
			}
		}
	}

	@Inject(at = @At("RETURN"), method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
	private void modifyBlockOutline(BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
		if (context instanceof EntityCollisionContext ctx) {
			if (ctx.getEntity() != null) {
				Entity entity = ctx.getEntity();
				if (PreventBlockActionPower.isSelectionPrevented(entity, pos, this::asState))
					cir.setReturnValue(Shapes.empty());
			}
		}
	}

	@Unique
	private boolean isAbove(Entity entity, VoxelShape shape, BlockPos pos, boolean defaultValue) {
		return entity.getY() > (double) pos.getY() + shape.max(Direction.Axis.Y) - (entity.isOnGround() ? 8.05 / 16.0 : 0.0015);
	}

	@Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
	private void preventCollisionWhenPhasing(Level world, BlockPos pos, Entity entity, CallbackInfo ci) {
		if (entity instanceof LivingEntity living && PhasingPower.shouldPhaseThrough(living, pos))
			ci.cancel();
	}
}
