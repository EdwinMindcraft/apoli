package io.github.apace100.apoli.mixin.forge;

import io.github.edwinmindcraft.apoli.common.power.EntityActionPower;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.caelus.common.CaelusApiImpl;

@Mixin(CaelusApiImpl.class)
public class CaelusMixin {

	@Inject(method = "canFly", at = @At("HEAD"), cancellable = true, remap = false)
	public void elytraFlightHook(LivingEntity living, CallbackInfoReturnable<Boolean> cir) {
		if (EntityActionPower.execute(ApoliPowers.PREVENT_ELYTRA_FLIGHT.get(), living))
			cir.setReturnValue(false);
	}
}
