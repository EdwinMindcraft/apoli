package io.github.apace100.apoli.mixin;

import dev.experimental.apoli.common.power.PhasingPower;
import dev.experimental.apoli.common.power.PreventBlockActionPower;
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
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("deprecation")
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class AbstractBlockStateMixin {


	@Shadow
	protected abstract BlockState asState();

	@Shadow
	public abstract VoxelShape getShape(BlockGetter world, BlockPos pos);

	@Shadow
	public abstract Block getBlock();

	@Inject(at = @At("HEAD"), method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
	private void phaseThroughBlocks(BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> info) {
		VoxelShape blockShape = this.getBlock().getCollisionShape(this.asState(), world, pos, context);
		if (!blockShape.isEmpty() && context instanceof EntityCollisionContext esc) {
			if (esc.getEntity().isPresent()) {
				Entity entity = esc.getEntity().get();
				boolean isAbove = this.isAbove(entity, blockShape, pos, false);
				if (world instanceof LevelReader reader && entity instanceof LivingEntity living && PhasingPower.shouldPhaseThrough(living, new BlockInWorld(reader, pos, true), isAbove))
					info.setReturnValue(Shapes.empty());
			}
		}
	}

	@Inject(at = @At("RETURN"), method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
	private void modifyBlockOutline(BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
		if (context instanceof EntityCollisionContext) {
			if (((EntityCollisionContext) context).getEntity().isPresent()) {
				Entity entity = ((EntityCollisionContext) context).getEntity().get();
				if (PreventBlockActionPower.isSelectionPrevented(entity, pos))
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
