package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.EntityGlowPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    @Shadow public LocalPlayer player;

    @Inject(method = "hasOutline", at = @At("RETURN"), cancellable = true)
    private void makeEntitiesGlow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if(!cir.getReturnValue()) {
            if(this.player != null) {
                if(player != entity && entity instanceof LivingEntity) {
                    if(PowerHolderComponent.getPowers(player, EntityGlowPower.class).stream().anyMatch(p -> p.doesApply(entity))) {
                        cir.setReturnValue(true);
                    }
                }
            }

        }
    }
}
