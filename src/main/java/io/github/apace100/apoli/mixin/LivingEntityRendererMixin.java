package io.github.apace100.apoli.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.common.power.PreventFeatureRenderPower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ColorConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin extends EntityRenderer<LivingEntity> {

	protected LivingEntityRendererMixin(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Inject(method = "isShaking", at = @At("HEAD"), cancellable = true)
	private void letPlayersShakeTheirBodies(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		if (IPowerContainer.hasPower(entity, ApoliPowers.SHAKING.get()))
			cir.setReturnValue(true);
	}

	@ModifyVariable(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;", shift = At.Shift.BEFORE))
	private RenderType changeRenderLayerWhenTranslucent(RenderType original, LivingEntity entity) {
		if (entity instanceof Player) {
			return ColorConfiguration.forPower(entity, ApoliPowers.MODEL_COLOR.get()).filter(x -> x.alpha() < 1F)
					.map(x -> RenderType.itemEntityTranslucentCull(this.getTextureLocation(entity))).orElse(original);
		}
		return original;
	}

	@Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"))
	private void renderColorChangedModel(EntityModel<LivingEntity> model, PoseStack postStack, VertexConsumer vertexConsumer, int p1, int overlay, float red, float green, float blue, float alpha, LivingEntity living) {
		Optional<ColorConfiguration> opt = ColorConfiguration.forPower(living, ApoliPowers.MODEL_COLOR.get());
		if (opt.isPresent()) {
			ColorConfiguration color = opt.get();
			red *= color.red();
			green *= color.green();
			blue *= color.blue();
			alpha *= color.alpha();
		}
		model.renderToBuffer(postStack, vertexConsumer, p1, overlay, red, green, blue, alpha);
	}
/*
	//FIXME: Something is wrong with mixin's ClassGenerators, ModifyArgs cannot generate a class.
	// When this is fixed, I'll use this again, but for now, I'm using a redirect.
	@OnlyIn(Dist.CLIENT)
	@ModifyArgs(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", ordinal = 0)
	)
	private void renderColorChangedModel(Args args, LivingEntity living, float f1, float f2, PoseStack ps, MultiBufferSource source, int i) {
		Optional<ColorConfiguration> colorConfiguration = ColorConfiguration.forPower(living, ApoliPowers.MODEL_COLOR.get());
		if (colorConfiguration.isPresent()) {
			ColorConfiguration color = colorConfiguration.get();
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
		}
	}*/

	//TODO This would be more suited to a coremod since it could do continue without having to use a Redirect.
	@Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/RenderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/Entity;FFFFFF)V"))
	private <T extends Entity> void preventFeatureRendering(RenderLayer<T, ?> instance, PoseStack poseStack, MultiBufferSource buffer, int packedLight, T living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (PreventFeatureRenderPower.doesPrevent(living, instance))
			return;
		instance.render(poseStack, buffer, packedLight, living, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
	}
}
