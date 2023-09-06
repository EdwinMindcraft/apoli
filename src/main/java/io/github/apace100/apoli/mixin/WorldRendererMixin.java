package io.github.apace100.apoli.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apace100.apoli.ApoliClient;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.edwinmindcraft.apoli.common.power.PhasingPower;
import io.github.edwinmindcraft.apoli.common.power.configuration.PhasingConfiguration;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {

	@Shadow
	public abstract void allChanged();

	@Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
	private void skipSkyRenderingForPhasingBlindness(PoseStack matrices, Matrix4f p_202425_, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
		if (camera.getEntity() instanceof LivingEntity living && PhasingPower.hasRenderMethod(living, PhasingConfiguration.RenderType.BLINDNESS) && MiscUtil.getInWallBlockState(living) != null)
			ci.cancel();
	}

	@Inject(method = "renderLevel", at = @At("HEAD"))
	private void updateChunksIfRenderChanged(PoseStack outlinebuffersource, float i, long j, boolean k, Camera l, GameRenderer i1, LightTexture multibuffersource, Matrix4f matrix4f, CallbackInfo ci) {
		if (ApoliClient.shouldReloadWorldRenderer) {
			this.allChanged();
			ApoliClient.shouldReloadWorldRenderer = false;
		}
	}
}
