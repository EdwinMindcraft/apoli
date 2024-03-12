package io.github.apace100.apoli.mixin;

import com.google.common.collect.ImmutableList;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.ModifyBlockRenderPower;
import io.github.edwinmindcraft.apoli.common.power.ModifyFluidRenderPower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyBlockRenderConfiguration;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyFluidRenderConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mixin(RenderChunkRegion.class)
public abstract class ChunkRendererRegionMixin {

	@Unique
	private List<ConfiguredPower<ModifyBlockRenderConfiguration, ModifyBlockRenderPower>> apoli$blockRender;
	@Unique
	private List<ConfiguredPower<ModifyFluidRenderConfiguration, ModifyFluidRenderPower>> apoli$fluidRender;

	@Shadow
	public abstract BlockState getBlockState(BlockPos pPos);

	@Inject(method = "getBlockState", at = @At("RETURN"), cancellable = true)
	private void modifyBlockRender(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
		Minecraft client = Minecraft.getInstance();
		if (client.level != null && client.player != null) {
			if (this.apoli$blockRender == null)
				this.apoli$blockRender = PowerContainer.getPowers(client.player, ApoliPowers.MODIFY_BLOCK_RENDER.get()).stream().filter(Holder::isBound).map(Holder::value).collect(ImmutableList.toImmutableList());
			if (this.apoli$blockRender.isEmpty()) return;
			this.apoli$blockRender.stream()
					.filter(x -> x.getFactory().check(x, client.level, pos, cir::getReturnValue))
					.map(x -> x.getConfiguration().block().defaultBlockState())
					.findFirst().ifPresent(cir::setReturnValue);
		}
	}

	@Inject(method = "getFluidState", at = @At("RETURN"), cancellable = true)
	private void modifyFluidRender(BlockPos pos, CallbackInfoReturnable<FluidState> cir) {
		Minecraft client = Minecraft.getInstance();
		if (client.level != null && client.player != null) {
			if (this.apoli$fluidRender == null)
				this.apoli$fluidRender = PowerContainer.getPowers(client.player, ApoliPowers.MODIFY_FLUID_RENDER.get()).stream().filter(Holder::isBound).map(Holder::value).collect(ImmutableList.toImmutableList());
			if (this.apoli$fluidRender.isEmpty()) return;
			this.apoli$fluidRender.stream()
					.filter(x -> x.getFactory().check(x, client.level, pos, () -> this.getBlockState(pos), cir.getReturnValue()))
					.map(x -> x.getConfiguration().fluid().defaultFluidState())
					.findFirst().ifPresent(cir::setReturnValue);
		}
	}
}
