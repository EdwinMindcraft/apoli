package io.github.apace100.apoli.mixin.forge;

import io.github.edwinmindcraft.apoli.common.power.EntityGlowPower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ColorConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@Mixin(LevelRenderer.class)
public class ChangeGlowColorMixin {

	@Shadow @Final private Minecraft minecraft;

	//I Still can't get @ModifyArgs to not crash my game.
	//This causes a crash with citadel
	@Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I"))
	private int setColors(Entity instance) {
		if (this.minecraft.getCameraEntity() == null)
			return instance.getTeamColor();
		Optional<ColorConfiguration> glowColor = EntityGlowPower.getGlowColor(this.minecraft.getCameraEntity(), instance);
		return glowColor.map(ColorConfiguration::asRGB).orElseGet(instance::getTeamColor);
	}
}
