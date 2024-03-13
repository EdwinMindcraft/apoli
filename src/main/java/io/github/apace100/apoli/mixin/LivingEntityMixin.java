package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.access.HiddenEffectStatus;
import io.github.apace100.apoli.access.ModifiableFoodEntity;
import io.github.apace100.apoli.util.StackPowerUtil;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.ApoliCommon;
import io.github.edwinmindcraft.apoli.common.network.S2CSyncAttacker;
import io.github.edwinmindcraft.apoli.common.power.*;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyFoodConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ModifiableFoodEntity {

	public LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

   /* @Inject(method = "onEffectAdded", at = @At("TAIL"))
    private void updateStatusEffectWhenApplied(StatusEffectInstance effectInstance, Entity source, CallbackInfo ci) {
        SyncStatusEffectsUtil.sendStatusEffectUpdatePacket((LivingEntity)(Object)this, SyncStatusEffectsUtil.UpdateType.APPLY, effectInstance);
    }

    @Inject(method = "onEffectUpdated", at = @At("TAIL"))
    private void updateStatusEffectWhenUpgraded(StatusEffectInstance effectInstance, boolean reapplyEffect, Entity source, CallbackInfo ci) {
        SyncStatusEffectsUtil.sendStatusEffectUpdatePacket((LivingEntity)(Object)this, SyncStatusEffectsUtil.UpdateType.UPGRADE, effectInstance);
    }

    @Inject(method = "onEffectRemoved", at = @At("TAIL"))
    private void updateStatusEffectWhenRemoved(StatusEffectInstance effectInstance, CallbackInfo ci) {
        SyncStatusEffectsUtil.sendStatusEffectUpdatePacket((LivingEntity)(Object)this, SyncStatusEffectsUtil.UpdateType.REMOVE, effectInstance);
    }

    @Inject(method = "clearStatusEffects", at = @At("RETURN"))
    private void updateStatusEffectWhenCleared(CallbackInfoReturnable<Boolean> cir) {
        SyncStatusEffectsUtil.sendStatusEffectUpdatePacket((LivingEntity)(Object)this, SyncStatusEffectsUtil.UpdateType.CLEAR, null);
    }*/

	@ModifyVariable(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), argsOnly = true)
	private MobEffectInstance modifyStatusEffect(MobEffectInstance effect) {
		MobEffect effectType = effect.getEffect();
		int originalAmp = effect.getAmplifier();
		int originalDur = effect.getDuration();

		int amplifier = Math.round(IPowerContainer.modify(this, ApoliPowers.MODIFY_STATUS_EFFECT_AMPLIFIER.get(), originalAmp, power -> ModifyStatusEffectPower.doesApply(power, effectType)));
		int duration = Math.round(IPowerContainer.modify(this, ApoliPowers.MODIFY_STATUS_EFFECT_DURATION.get(), originalDur, power -> ModifyStatusEffectPower.doesApply(power, effectType)));

		if (amplifier != originalAmp || duration != originalDur) {
			return new MobEffectInstance(
					effectType,
					duration,
					amplifier,
					effect.isAmbient(),
					effect.isVisible(),
					effect.showIcon(),
					((HiddenEffectStatus) effect).getHiddenEffect()
			);
		}
		return effect;
	}

	@Inject(method = "setLastHurtByMob", at = @At("TAIL"))
	private void syncAttacker(LivingEntity attacker, CallbackInfo ci) {
		if (!this.level.isClientSide()) {
			ApoliCommon.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new S2CSyncAttacker(this.getId(), attacker != null ? OptionalInt.of(attacker.getId()) : OptionalInt.empty()));
		}
	}

	@Inject(method = "collectEquipmentChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeMap;removeAttributeModifiers(Lcom/google/common/collect/Multimap;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void removeEquipmentPowers(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir, Map<EquipmentSlot, ItemStack> map, EquipmentSlot[] var2, int var3, int var4, EquipmentSlot equipmentSlot, ItemStack itemStack3, ItemStack itemStack4) {
		List<StackPowerUtil.StackPower> powers = StackPowerUtil.getPowers(itemStack3, equipmentSlot);
		if (powers.size() > 0) {
			ResourceLocation source = new ResourceLocation(Apoli.MODID, equipmentSlot.getName());
			IPowerContainer.get(this).ifPresent(container -> {
				powers.forEach(sp -> container.removePower(sp.powerId, source));
				container.sync();
			});
		}
	}

	@Inject(method = "collectEquipmentChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeMap;addTransientAttributeModifiers(Lcom/google/common/collect/Multimap;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void addEquipmentPowers(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir, Map<EquipmentSlot, ItemStack> map, EquipmentSlot[] var2, int var3, int var4, EquipmentSlot equipmentSlot, ItemStack itemStack3, ItemStack itemStack4) {
		List<StackPowerUtil.StackPower> powers = StackPowerUtil.getPowers(itemStack4, equipmentSlot);
		if (powers.size() > 0) {
			ResourceLocation source = new ResourceLocation(Apoli.MODID, equipmentSlot.getName());
			IPowerContainer.get(this).ifPresent(container -> {
				powers.forEach(sp -> container.addPower(sp.powerId, source));
				container.sync();
			});
		} else if (StackPowerUtil.getPowers(itemStack3, equipmentSlot).size() > 0) {
			IPowerContainer.sync(this);
		}
	}

    @Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
    private void modifyWalkableFluids(FluidState fluid, CallbackInfoReturnable<Boolean> cir) {
        if (IPowerContainer.getPowers(this, ApoliPowers.WALK_ON_FLUID.get()).stream().anyMatch(p -> fluid.is(p.getConfiguration().value())))
            cir.setReturnValue(true);
    }

    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInWaterRainOrBubble()Z"))
    private boolean preventExtinguishingFromSwimming(LivingEntity livingEntity) {
        if (livingEntity.isSwimming() && this.getFluidHeight(FluidTags.WATER) <= 0 && IPowerContainer.hasPower(livingEntity, ApoliPowers.SWIMMING.get()))
            return false;
        return livingEntity.isInWaterRainOrBubble();
    }

	@Unique
	private boolean prevPowderSnowState = false;

	@Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getTicksFrozen()I"))
	private void freezeEntityFromPower(CallbackInfo ci) {
		if (IPowerContainer.hasPower(this, ApoliPowers.FREEZE.get())) {
			this.prevPowderSnowState = this.isInPowderSnow;
			this.isInPowderSnow = true;
		}
	}

	@Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeFrost()V"))
	private void unfreezeEntityFromPower(CallbackInfo ci) {
		if (IPowerContainer.hasPower(this, ApoliPowers.FREEZE.get()))
			this.isInPowderSnow = this.prevPowderSnowState;
	}

	@Inject(method = "canFreeze", at = @At("RETURN"), cancellable = true)
	private void allowFreezingPower(CallbackInfoReturnable<Boolean> cir) {
		if (IPowerContainer.hasPower(this, ApoliPowers.FREEZE.get()))
			cir.setReturnValue(true);
	}

	// SetEntityGroupPower
	@Inject(at = @At("HEAD"), method = "getMobType", cancellable = true)
	public void getGroup(CallbackInfoReturnable<MobType> info) {
		List<ConfiguredPower<FieldConfiguration<MobType>, EntityGroupPower>> powers = IPowerContainer.getPowers(this, ApoliPowers.ENTITY_GROUP.get());
		if (powers.size() > 0) {
			if (powers.size() > 1) {
				Apoli.LOGGER.warn("Entity " + this.getDisplayName() + " has two instances of SetEntityGroupPower.");
			}
			info.setReturnValue(powers.get(0).getConfiguration().value());
		}
	}

	@Inject(method = "doPush", at = @At("HEAD"), cancellable = true)
	private void preventPushing(Entity entity, CallbackInfo ci) {
		if (BiEntityConditionPower.any(ApoliPowers.PREVENT_ENTITY_COLLISION.get(), this, this, entity) ||
			BiEntityConditionPower.any(ApoliPowers.PREVENT_ENTITY_COLLISION.get(), entity, entity, this)) {
			ci.cancel();
		}
	}

	@Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;getValue()D", ordinal = 0))
	public void modifyFall(Vec3 motion, CallbackInfo ci) {
		ModifyFallingPower.apply(this, this.getDeltaMovement().y <= 0.0D);
	}

	@ModifyVariable(method = "eat", at = @At("HEAD"), argsOnly = true)
	private ItemStack modifyEatenItemStack(ItemStack original) {
		if ((Object) this instanceof Player) {
			return original;
		}
		List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> mfps = ModifyFoodPower.getValidPowers(this, original);
		MutableObject<ItemStack> stack = new MutableObject<>(original.copy());
		ModifyFoodPower.modifyStack(mfps, this.level, stack);
		((ModifiableFoodEntity) this).setCurrentModifyFoodPowers(mfps);
		((ModifiableFoodEntity) this).setOriginalFoodStack(original);
		return stack.getValue();
	}

	@ModifyVariable(method = "eat",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V", shift = At.Shift.AFTER),
			argsOnly = true)
	private ItemStack unmodifyEatenItemStack(ItemStack modified) {
		ModifiableFoodEntity foodEntity = this;
		ItemStack original = foodEntity.getOriginalFoodStack();
		if (original != null) {
			foodEntity.setOriginalFoodStack(null);
			return original;
		}
		return modified;
	}

	@Inject(method = "eat", at = @At("TAIL"))
	private void removeCurrentModifyFoodPowers(Level world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
		this.setCurrentModifyFoodPowers(new LinkedList<>());
	}

	@Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V"))
	private void preventApplyingFoodEffects(LivingEntity livingEntity, ItemStack stack, Level world, LivingEntity targetEntity) {
		if (this.getCurrentModifyFoodPowers().stream().anyMatch(x -> x.getConfiguration().preventEffects()))
			return;
		this.addEatEffect(stack, world, targetEntity);
	}

	@Shadow
	protected abstract void addEatEffect(ItemStack p_21064_, Level p_21065_, LivingEntity p_21066_);

	@Shadow
	public float flyingSpeed;

	@Inject(method = "getFrictionInfluencedSpeed(F)F", at = @At("RETURN"), cancellable = true)
	private void modifyFlySpeed(float slipperiness, CallbackInfoReturnable<Float> cir) {
		if (!this.onGround)
			cir.setReturnValue(IPowerContainer.modify(this, ApoliPowers.MODIFY_AIR_SPEED.get(), this.flyingSpeed));
	}

	@Unique
	private List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> apoli$currentModifyFoodPowers = new LinkedList<>();

	@Unique
	private ItemStack apoli$originalFoodStack;

	@Override
	public List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> getCurrentModifyFoodPowers() {
		return this.apoli$currentModifyFoodPowers;
	}

	@Override
	public void setCurrentModifyFoodPowers(List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> powers) {
		this.apoli$currentModifyFoodPowers = powers;
	}

	@Override
	public ItemStack getOriginalFoodStack() {
		return this.apoli$originalFoodStack;
	}

	@Override
	public void setOriginalFoodStack(ItemStack original) {
		this.apoli$originalFoodStack = original;
	}

	@Unique
	private boolean apoli$shouldSyncFoodData = false;

	@Override
	public void enforceFoodSync() {
		this.apoli$shouldSyncFoodData = true;
	}

	@Override
	public void resetFoodSync() {
		this.apoli$shouldSyncFoodData = false;
	}

	@Override
	public boolean shouldSyncFood() {
		return this.apoli$shouldSyncFoodData;
	}
}
