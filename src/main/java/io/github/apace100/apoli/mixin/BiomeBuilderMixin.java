package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.access.BiomeWeatherAccess;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Biome.BiomeBuilder.class)
public class BiomeBuilderMixin {

    @Shadow
    private @Nullable Float downfall;

    @Inject(method = "build", at = @At("RETURN"))
    private void apoli$storeDownfall(CallbackInfoReturnable<Biome> cir) {
        ((BiomeWeatherAccess)(Object)cir.getReturnValue()).setDownfall(downfall.floatValue());
    }
}