package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.access.ModifiableFoodEntity;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.ModifyFoodPower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyFoodConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import io.github.edwinmindcraft.apoli.common.util.CoreUtils;
import io.github.edwinmindcraft.apoli.common.util.LivingDamageCache;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Nameable, CommandSource {

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@Shadow
	public abstract boolean hurt(@NotNull DamageSource source, float amount);

	@Shadow
	public abstract @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot slot);

	@Shadow
	public abstract @NotNull EntityDimensions getDimensions(@NotNull Pose pose);

	@Shadow
	@Nullable
	public abstract ItemEntity drop(ItemStack p_36179_, boolean p_36180_, boolean p_36181_);

	@Shadow
	public abstract Inventory getInventory();

	@Inject(method = "updateSwimming", at = @At("TAIL"))
	private void updateSwimmingPower(CallbackInfo ci) {
		LazyOptional<IPowerContainer> lazyContainer = IPowerContainer.get(this);
		if (!lazyContainer.isPresent())
			return;
		IPowerContainer container = lazyContainer.orElseThrow(RuntimeException::new);
		if (container.hasPower(ApoliPowers.SWIMMING.get())) {
			this.setSwimming(this.isSprinting() && !this.isPassenger());
			this.wasTouchingWater = this.isSwimming();
			if (this.isSwimming()) {
				this.fallDistance = 0.0F;
				Vec3 look = this.getLookAngle();
				this.move(MoverType.SELF, new Vec3(look.x / 4, look.y / 4, look.z / 4));
			}
		} else if (container.hasPower(ApoliPowers.IGNORE_WATER.get()))
			this.setSwimming(false);
	}

	@ModifyVariable(method = "eat", at = @At("HEAD"), argsOnly = true)
	private ItemStack modifyEatenItemStack(ItemStack original) {
		List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> mfps = ModifyFoodPower.getValidPowers(this, original);
		MutableObject<ItemStack> stack = new MutableObject<>(original.copy());
		ModifyFoodPower.modifyStack(mfps, this.level(), stack);
		((ModifiableFoodEntity) this).setCurrentModifyFoodPowers(mfps);
		((ModifiableFoodEntity) this).setOriginalFoodStack(original);
		return stack.getValue();
	}

	// ModifyExhaustion
	@ModifyVariable(at = @At("HEAD"), method = "causeFoodExhaustion", ordinal = 0, name = "exhaustion", argsOnly = true)
	private float modifyExhaustion(float exhaustionIn) {
		return IPowerContainer.modify(this, ApoliPowers.MODIFY_EXHAUSTION.get(), exhaustionIn);
	}

	// Prevent healing if DisableRegenPower
	// Note that this function was called "shouldHeal" instead of "canFoodHeal" at some point in time.
    /*@Inject(method = "canFoodHeal", at = @At("HEAD"), cancellable = true)
    private void disableHeal(CallbackInfoReturnable<Boolean> info) {
        if(PowerHolderComponent.hasPower(this, DisableRegenPower.class)) {
            info.setReturnValue(false);
        }
    }*/

	@Inject(method = "canTakeItem", at = @At("HEAD"), cancellable = true)
	private void preventArmorDispensing(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
		EquipmentSlot slot = Mob.getEquipmentSlotForItem(stack);
		if (CoreUtils.isItemForbidden(this, slot, stack))
			info.setReturnValue(false);
	}

	@Inject(method = "hurt", at = @At("RETURN"), cancellable = true)
	private void performDamageBypass(DamageSource pSource, float pAmount, CallbackInfoReturnable<Boolean> cir) {
		if (((LivingDamageCache) this).bypassesDamageCheck() && pAmount == 0)
			cir.setReturnValue(super.hurt(pSource, pAmount));
	}
}
