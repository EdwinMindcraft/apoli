package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.access.EntityAttributeInstanceAccess;
import io.github.apace100.apoli.access.HiddenEffectStatus;
import io.github.apace100.apoli.access.ModifiableFoodEntity;
import io.github.apace100.apoli.util.StackPowerUtil;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.VariableAccess;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.ApoliCommon;
import io.github.edwinmindcraft.apoli.common.network.S2CPlayerDismount;
import io.github.edwinmindcraft.apoli.common.network.S2CSyncAttacker;
import io.github.edwinmindcraft.apoli.common.power.*;
import io.github.edwinmindcraft.apoli.common.power.configuration.ActionOnItemUseConfiguration;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyDamageDealtConfiguration;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyDamageTakenConfiguration;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyFoodConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import io.github.edwinmindcraft.apoli.common.util.LivingDamageCache;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ModifiableFoodEntity, LivingDamageCache {

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

		int amplifier = Math.round(PowerContainer.modify(this, ApoliPowers.MODIFY_STATUS_EFFECT_AMPLIFIER.get(), originalAmp, power -> ModifyStatusEffectPower.doesApply(power.value(), effectType)));
		int duration = Math.round(PowerContainer.modify(this, ApoliPowers.MODIFY_STATUS_EFFECT_DURATION.get(), originalDur, power -> ModifyStatusEffectPower.doesApply(power.value(), effectType)));

		if (amplifier != originalAmp || duration != originalDur) {
			return new MobEffectInstance(effectType, duration, amplifier, effect.isAmbient(), effect.isVisible(), effect.showIcon(), ((HiddenEffectStatus) effect).getHiddenEffect(), effect.getFactorData());
		}
		return effect;
	}

	@Inject(method = "setLastHurtByMob", at = @At("TAIL"))
	private void syncAttacker(LivingEntity attacker, CallbackInfo ci) {
		if (!this.level().isClientSide()) {
			ApoliCommon.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new S2CSyncAttacker(this.getId(), attacker != null ? OptionalInt.of(attacker.getId()) : OptionalInt.empty()));
		}
	}

	@Inject(method = "collectEquipmentChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeMap;removeAttributeModifiers(Lcom/google/common/collect/Multimap;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void removeEquipmentPowers(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir, Map<EquipmentSlot, ItemStack> map, EquipmentSlot[] var2, int var3, int var4, EquipmentSlot equipmentSlot, ItemStack itemStack3, ItemStack itemStack4) {
		List<StackPowerUtil.StackPower> powers = StackPowerUtil.getPowers(itemStack3, equipmentSlot);
		if (powers.size() > 0) {
			ResourceLocation source = new ResourceLocation(Apoli.MODID, equipmentSlot.getName());
			PowerContainer.get(this).ifPresent(container -> {
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
			PowerContainer.get(this).ifPresent(container -> {
				powers.forEach(sp -> container.addPower(sp.powerId, source));
				container.sync();
			});
		} else if (StackPowerUtil.getPowers(itemStack3, equipmentSlot).size() > 0) {
			PowerContainer.sync(this);
		}
	}

	@Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
	private void modifyWalkableFluids(FluidState fluid, CallbackInfoReturnable<Boolean> cir) {
		if (PowerContainer.getPowers(this, ApoliPowers.WALK_ON_FLUID.get()).stream().anyMatch(p -> fluid.is(p.value().getConfiguration().value())))
			cir.setReturnValue(true);
	}

	private List<Holder<ConfiguredPower<ModifyDamageTakenConfiguration, ModifyDamageTakenPower>>> apoli$damageMods;
	private List<Holder<ConfiguredPower<ModifyDamageDealtConfiguration, ModifyDamageDealtPower>>> apoli$damageSourceMods;
	private boolean apoli$bypassDamageCheck = false;
	private int apoli$shouldApplyArmor;
	private int apoli$shouldDamageArmor;

	@Override
	public List<Holder<ConfiguredPower<ModifyDamageTakenConfiguration, ModifyDamageTakenPower>>> getModifyDamageTakenPowers() {return this.apoli$damageMods;}

	@Override
	public List<Holder<ConfiguredPower<ModifyDamageDealtConfiguration, ModifyDamageDealtPower>>> getModifyDamageDealtPowers() {return this.apoli$damageSourceMods;}

	@Override
	public void setModifyDamageTakenPowers(List<Holder<ConfiguredPower<ModifyDamageTakenConfiguration, ModifyDamageTakenPower>>> values) {this.apoli$damageMods = values;}

	@Override
	public void setModifyDamageDealtPowers(List<Holder<ConfiguredPower<ModifyDamageDealtConfiguration, ModifyDamageDealtPower>>> values) {this.apoli$damageSourceMods = values;}

	@Override
	public void bypassDamageCheck(boolean value) {this.apoli$bypassDamageCheck = value;}

	@Override
	public boolean bypassesDamageCheck() {return this.apoli$bypassDamageCheck;}

	@Override
	public void setArmorValues(int apply, int damage) {
		this.apoli$shouldApplyArmor = apply;
		this.apoli$shouldDamageArmor = damage;
	}

	@Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true)
	private void modifyArmorApplicance(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
		if (this.apoli$shouldApplyArmor != 0) {
			if (this.apoli$shouldDamageArmor > 0) this.hurtArmor(source, amount);
			if (this.apoli$shouldApplyArmor > 0) {
				if (this.apoli$shouldDamageArmor == 0) this.hurtArmor(source, amount);
				float damageLeft = CombatRules.getDamageAfterAbsorb(amount, this.getArmorValue(), (float) this.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
				cir.setReturnValue(damageLeft);
			} else {
				cir.setReturnValue(amount);
			}
		} else {
			if (this.apoli$shouldDamageArmor > 0 && source.is(DamageTypeTags.BYPASSES_ARMOR)) this.hurtArmor(source, amount);
		}
	}

	@ModifyArg(method = "getDamageAfterArmorAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtArmor(Lnet/minecraft/world/damagesource/DamageSource;F)V"), index = 1)
	private float preventArmorDamaging(float amount) {
		if (this.apoli$shouldDamageArmor < 0) {
            return 0;
        }
        return amount;
    }

	@Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInWaterRainOrBubble()Z"))
	private boolean preventExtinguishingFromSwimming(LivingEntity livingEntity) {
		if (livingEntity.isSwimming() && this.getFluidTypeHeight(ForgeMod.WATER_TYPE.get()) <= 0 && PowerContainer.hasPower(livingEntity, ApoliPowers.SWIMMING.get()))
			return false;
		return livingEntity.isInWaterRainOrBubble();
	}

	@Unique
	private boolean prevPowderSnowState = false;

	@Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getTicksFrozen()I"))
	private void freezeEntityFromPower(CallbackInfo ci) {
		if (PowerContainer.hasPower(this, ApoliPowers.FREEZE.get())) {
			this.prevPowderSnowState = this.isInPowderSnow;
			this.isInPowderSnow = true;
		}
	}

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInFluidType(Lnet/minecraft/world/level/material/FluidState;)Z"))
    private boolean dontCountEntityAsInFluid(LivingEntity instance, FluidState fluidState) {
        if (fluidState.getFluidType() == ForgeMod.WATER_TYPE.get()) {
            return false;
        }
        return instance.isInFluidType(fluidState);
    }

    @ModifyVariable(method = "aiStep", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private FluidType allowJumpingInIgnoredWater(FluidType fluidType) {
        if (PowerContainer.hasPower(this, ApoliPowers.IGNORE_WATER.get())) {
            return ForgeMod.EMPTY_TYPE.get();
        }
        return fluidType;
    }

	@Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeFrost()V"))
	private void unfreezeEntityFromPower(CallbackInfo ci) {
		if (PowerContainer.hasPower(this, ApoliPowers.FREEZE.get())) this.isInPowderSnow = this.prevPowderSnowState;
	}

	@Inject(method = "canFreeze", at = @At("RETURN"), cancellable = true)
	private void allowFreezingPower(CallbackInfoReturnable<Boolean> cir) {
		if (PowerContainer.hasPower(this, ApoliPowers.FREEZE.get())) cir.setReturnValue(true);
	}

	// Moved from PlayerEntityMixin.
	@Inject(method = "stopRiding", at = @At("HEAD"))
	private void sendPlayerDismountPacket(CallbackInfo ci) {
		if (!this.level().isClientSide() && this.getVehicle() instanceof ServerPlayer player) {
			ApoliCommon.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new S2CPlayerDismount(this.getId()));
		}
	}

	// SetEntityGroupPower
	@Inject(at = @At("HEAD"), method = "getMobType", cancellable = true)
	public void getGroup(CallbackInfoReturnable<MobType> info) {
		List<Holder<ConfiguredPower<FieldConfiguration<MobType>, EntityGroupPower>>> powers = PowerContainer.getPowers(this, ApoliPowers.ENTITY_GROUP.get());
		if (powers.size() > 0) {
			if (powers.size() > 1) {
				Apoli.LOGGER.warn("Entity " + this.getDisplayName() + " has two instances of SetEntityGroupPower.");
			}
			info.setReturnValue(powers.get(0).value().getConfiguration().value());
		}
	}

	@Inject(method = "getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D", at = @At("RETURN"), cancellable = true)
	private void modifyAttributeValue(Attribute attribute, CallbackInfoReturnable<Double> cir) {
		double originalValue = this.getAttributes().getValue(attribute);
		double modified = PowerContainer.modify(this, ApoliPowers.MODIFY_ATTRIBUTE.get(), (float) originalValue, p -> p.get().getConfiguration().attribute() == attribute);
		if (originalValue != modified) {
			cir.setReturnValue(modified);
		}
	}

	@Inject(method = "getAttribute", at = @At("RETURN"))
	private void setEntityToAttributeInstance(Attribute attribute, CallbackInfoReturnable<AttributeInstance> cir) {
		AttributeInstance instance = cir.getReturnValue();
		if (instance != null) {
			((EntityAttributeInstanceAccess)instance).setEntity(this);
		}
	}

	@Inject(method = "doPush", at = @At("HEAD"), cancellable = true)
	private void preventPushing(Entity entity, CallbackInfo ci) {
		if (BiEntityConditionPower.any(ApoliPowers.PREVENT_ENTITY_COLLISION.get(), this, this, entity) || BiEntityConditionPower.any(ApoliPowers.PREVENT_ENTITY_COLLISION.get(), entity, entity, this)) {
			ci.cancel();
		}
	}

	@ModifyVariable(method = "eat", at = @At("HEAD"), argsOnly = true)
	private ItemStack modifyEatenItemStack(ItemStack original) {
		if ((Object) this instanceof Player) {
			return original;
		}
		List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> mfps = ModifyFoodPower.getValidPowers(this, original);
		MutableObject<ItemStack> stack = new MutableObject<>(original.copy());
		ModifyFoodPower.modifyStack(mfps, this.level(), stack);
		((ModifiableFoodEntity) this).setCurrentModifyFoodPowers(mfps);
		((ModifiableFoodEntity) this).setOriginalFoodStack(original);
		return stack.getValue();
	}

	@ModifyVariable(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V", shift = At.Shift.AFTER), argsOnly = true)
	private ItemStack unmodifyEatenItemStack(ItemStack modified) {
		ModifiableFoodEntity foodEntity = this;
		ItemStack original = foodEntity.getOriginalFoodStack();
		if (original != null) {
			foodEntity.setOriginalFoodStack(null);
			return original;
		}
		return modified;
	}

    // Moved from ItemStackMixin.
    @Inject(method = "releaseUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onUseItemStop(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;I)Z"))
    private void callActionOnUseStopBefore(CallbackInfo ci) {
        if (ApoliAPI.getPowerContainer(this) != null && ApoliAPI.getPowerContainer(this).getPowers(ApoliPowers.ACTION_ON_ITEM_USE.get()).stream().anyMatch(p -> p.isBound() && p.value().getFactory().canRun(p, this, this.useItem, ActionOnItemUseConfiguration.TriggerType.STOP, ActionOnItemUseConfiguration.PriorityPhase.BEFORE))) {
            Mutable<ItemStack> mutable = VariableAccess.hand((LivingEntity)(Object) this, this.getUsedItemHand());
            ActionOnItemUsePower.execute(this, this.useItem, mutable,
                    ActionOnItemUseConfiguration.TriggerType.STOP, ActionOnItemUseConfiguration.PriorityPhase.BEFORE);
            this.useItem = mutable.getValue();
        }
    }

    // Moved from ItemStackMixin.
    @ModifyVariable(method = "updateUsingItem", at = @At("HEAD"), argsOnly = true)
    private ItemStack callActionOnUseDuringBefore(ItemStack original) {
        if (ApoliAPI.getPowerContainer(this) != null && ApoliAPI.getPowerContainer(this).getPowers(ApoliPowers.ACTION_ON_ITEM_USE.get()).stream().anyMatch(p -> p.isBound() && p.value().getFactory().canRun(p, this, original, ActionOnItemUseConfiguration.TriggerType.DURING, ActionOnItemUseConfiguration.PriorityPhase.BEFORE))) {
            Mutable<ItemStack> mutable = VariableAccess.hand((LivingEntity)(Object) this, this.getUsedItemHand());
            ActionOnItemUsePower.execute(this, original, mutable, ActionOnItemUseConfiguration.TriggerType.DURING, ActionOnItemUseConfiguration.PriorityPhase.BEFORE);
            if (!ItemStack.matches(mutable.getValue(), original)) {
                return mutable.getValue();
            }
        }
        return original;
    }

    // Moved from ItemStackMixin.
    @Inject(method = "completeUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;triggerItemUseEffects(Lnet/minecraft/world/item/ItemStack;I)V"))
    private void callActionOnUseFinishBefore(CallbackInfo ci) {
        if (ApoliAPI.getPowerContainer(this) != null && ApoliAPI.getPowerContainer(this).getPowers(ApoliPowers.ACTION_ON_ITEM_USE.get()).stream().anyMatch(p -> p.isBound() && p.value().getFactory().canRun(p, this, this.useItem, ActionOnItemUseConfiguration.TriggerType.FINISH, ActionOnItemUseConfiguration.PriorityPhase.BEFORE))) {
            Mutable<ItemStack> mutable = new MutableObject<>(this.useItem);
            ActionOnItemUsePower.execute(this, this.useItem, mutable, ActionOnItemUseConfiguration.TriggerType.FINISH, ActionOnItemUseConfiguration.PriorityPhase.BEFORE);
            this.useItem = mutable.getValue();
        }
    }

	@Inject(method = "eat", at = @At("TAIL"))
	private void removeCurrentModifyFoodPowers(Level world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
		this.setCurrentModifyFoodPowers(new LinkedList<>());
	}

	@Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V"))
	private void preventApplyingFoodEffects(LivingEntity livingEntity, ItemStack stack, Level world, LivingEntity targetEntity) {
		if (this.getCurrentModifyFoodPowers().stream().anyMatch(x -> x.getConfiguration().preventEffects())) return;
		this.addEatEffect(stack, world, targetEntity);
	}

	@Shadow
	protected abstract void addEatEffect(ItemStack p_21064_, Level p_21065_, LivingEntity p_21066_);

	@Shadow
	protected abstract void hurtArmor(DamageSource pDamageSource, float pDamageAmount);

	@Shadow
	public abstract int getArmorValue();

	@Shadow
	public abstract double getAttributeValue(Attribute pAttribute);

	@Shadow
	public abstract AttributeMap getAttributes();

    @Shadow protected abstract float getFlyingSpeed();

    @Shadow protected ItemStack useItem;

    @Shadow public abstract InteractionHand getUsedItemHand();

    @Inject(method = "getFrictionInfluencedSpeed(F)F", at = @At("RETURN"), cancellable = true)
	private void modifyFlySpeed(float slipperiness, CallbackInfoReturnable<Float> cir) {
		if (!this.onGround())
			cir.setReturnValue(PowerContainer.modify(this, ApoliPowers.MODIFY_AIR_SPEED.get(), this.getFlyingSpeed()));
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
