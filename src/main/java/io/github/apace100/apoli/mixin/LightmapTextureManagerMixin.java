package io.github.apace100.apoli.mixin;

import io.github.edwinmindcraft.apoli.api.power.INightVisionPower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightTexture.class)
@OnlyIn(Dist.CLIENT)
public abstract class LightmapTextureManagerMixin implements AutoCloseable {

    @Shadow @Final private Minecraft minecraft;

    @ModifyVariable(method = "updateLightTexture", at = @At(value = "STORE"), ordinal = 4)
    private float nightVisionPowerEffect(float value) {
        return INightVisionPower.getNightVisionStrength(this.minecraft.player).map(x -> Math.max(x, value)).orElse(value);
    }
}
