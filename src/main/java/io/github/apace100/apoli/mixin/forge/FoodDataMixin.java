package io.github.apace100.apoli.mixin.forge;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class FoodDataMixin {
	@Unique
	private Player player;

	@Inject(method = "tick", at = @At("HEAD"))
	private void playerHook(Player player, CallbackInfo ci) {
		this.player = player;
	}

	/**
	 * Was moved here from PlayerEntityMixin, as the method is called isHurt on mojmap,
	 * which means more modders might use it.<br/>
	 * Injecting it here is, in my opinion, safer.
	 */
	@ModifyVariable(method = "tick", at = @At(value = "STORE"))
	private boolean preventNaturalRegeneration(boolean bool) {
		if (PowerContainer.hasPower(this.player, ApoliPowers.DISABLE_REGEN.get()))
			return false;
		return bool;
	}
}
