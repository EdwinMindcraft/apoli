package io.github.apace100.apoli.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.experimental.apoli.api.component.IPowerContainer;
import dev.experimental.apoli.common.power.InvisibilityPower;
import dev.experimental.apoli.common.power.configuration.ColorConfiguration;
import dev.experimental.apoli.common.registry.ModPowers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin extends EntityRenderer<LivingEntity> {

	protected LivingEntityRendererMixin(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Inject(method = "isShaking", at = @At("HEAD"), cancellable = true)
	private void letPlayersShakeTheirBodies(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		if (IPowerContainer.hasPower(entity, ModPowers.SHAKING.get()))
			cir.setReturnValue(true);
	}

	@ModifyVariable(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;", shift = At.Shift.BEFORE))
	private RenderType changeRenderLayerWhenTranslucent(RenderType original, LivingEntity entity) {
		if (entity instanceof Player) {
			return ColorConfiguration.forPower(entity, ModPowers.MODEL_COLOR.get()).filter(x -> x.alpha() < 1F)
					.map(x -> RenderType.itemEntityTranslucentCull(this.getTextureLocation(entity))).orElse(original);
		}
		return original;
	}

	@OnlyIn(Dist.CLIENT)
	@ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", ordinal = 0))
	private <T extends LivingEntity> void renderColorChangedModel(Args args, LivingEntity living) {
		ColorConfiguration.forPower(living, ModPowers.MODEL_COLOR.get()).ifPresent(color -> {
			//Mixin is being weird.
			//Basically: if there is a redirect, args[0] is a Model, otherwise args[0] is the PoseStack
			int red = args.size() - 4;
			int green = args.size() - 3;
			int blue = args.size() - 2;
			int alpha = args.size() - 1;
			args.set(red, args.<Float>get(red) * color.red());
			args.set(green, args.<Float>get(green) * color.green());
			args.set(blue, args.<Float>get(blue) * color.blue());
			args.set(alpha, args.<Float>get(alpha) * color.alpha());
		});
	}
}
