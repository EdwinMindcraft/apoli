package io.github.apace100.apoli.mixin;

import com.mojang.authlib.GameProfile;
import io.github.apace100.apoli.access.WaterMovingEntity;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayer implements WaterMovingEntity {

	private boolean isMoving = false;

	public ClientPlayerEntityMixin(ClientLevel world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(at = @At("HEAD"), method = "isUnderWater", cancellable = true)
	private void allowSwimming(CallbackInfoReturnable<Boolean> cir) {
		LazyOptional<IPowerContainer> lazyContainer = IPowerContainer.get(this);
		if (!lazyContainer.isPresent()) return;
		IPowerContainer container = lazyContainer.orElseThrow(RuntimeException::new);
		if (container.hasPower(ApoliPowers.SWIMMING.get())) {
			cir.setReturnValue(true);
		} else if (container.hasPower(ApoliPowers.IGNORE_WATER.get())) {
			cir.setReturnValue(false);
		}
	}

	@Inject(at = @At("HEAD"), method = "aiStep")
	private void beginMovementPhase(CallbackInfo ci) {
		this.isMoving = true;
	}

	@Inject(at = @At("TAIL"), method = "aiStep")
	private void endMovementPhase(CallbackInfo ci) {
		this.isMoving = false;
	}

	public boolean isInMovementPhase() {
		return this.isMoving;
	}

	@Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Abilities;getFlyingSpeed()F"))
	private float modifyFlySpeed(Abilities playerAbilities) {
		return IPowerContainer.modify(this, ApoliPowers.MODIFY_AIR_SPEED.get(), playerAbilities.getFlyingSpeed());
	}

	@ModifyVariable(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;onGround()Z"), ordinal = 4)
	private boolean modifySprintAbility(boolean original) {
		boolean prevent = IPowerContainer.hasPower(this, ApoliPowers.PREVENT_SPRINTING.get());
		return !prevent && original;
	}
}
