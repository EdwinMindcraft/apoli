package io.github.apace100.apoli.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.apace100.apoli.access.EntityLinkedItemStack;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.VariableAccess;
import io.github.edwinmindcraft.apoli.common.power.ActionOnItemUsePower;
import io.github.edwinmindcraft.apoli.common.power.ItemOnItemPower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ActionOnItemUseConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliCapabilities;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin  {

    @Shadow public abstract int getUseDuration(LivingEntity entity);

    @Shadow public abstract InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand);

    protected ItemStackMixin(Class<ItemStack> baseClass) {
        super(baseClass);
    }

    //Moved from ItemMixin to prevent overrides from other mods from interfering too much.
	@Inject(method = "overrideOtherStackedOnMe", at = @At("RETURN"), cancellable = true)
	public void forgeItem(ItemStack other, Slot slot, ClickAction pAction, Player pPlayer, SlotAccess otherAccess, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue())
			return;
		if (pAction != ClickAction.SECONDARY)
			return;
		if (ItemOnItemPower.execute(pPlayer, slot, otherAccess))
			cir.setReturnValue(true);
	}

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item callActionOnUseInstantBefore(ItemStack original, Level world, Player user, InteractionHand hand) {
        if (ApoliAPI.getPowerContainer(user) != null && ApoliAPI.getPowerContainer(user).getPowers(ApoliPowers.ACTION_ON_ITEM_USE.get()).stream().anyMatch(p -> p.isBound() && p.value().getFactory().canRun(p, user, (ItemStack)(Object)this, this.getUseDuration() == 0 ? ActionOnItemUseConfiguration.TriggerType.INSTANT : ActionOnItemUseConfiguration.TriggerType.START, ActionOnItemUseConfiguration.PriorityPhase.BEFORE))) {
            MutableObject<ItemStack> mutable = new MutableObject<>(original);
            ActionOnItemUsePower.execute(user, original, mutable, this.getUseDuration() == 0 ? ActionOnItemUseConfiguration.TriggerType.INSTANT : ActionOnItemUseConfiguration.TriggerType.START, ActionOnItemUseConfiguration.PriorityPhase.BEFORE);
            return mutable.getValue().getItem();
        }
        return original.getItem();
    }

    // This is probably the wrong way to do this but I really didn't want to set up NBT for the capability. Oh well.
    @Inject(method = "copy", at = @At(value = "RETURN"))
    private void copyNewParams(CallbackInfoReturnable<ItemStack> cir) {
        cir.getReturnValue().getCapability(ApoliCapabilities.ENTITY_LINKED_ITEM_STACK).ifPresent(eli -> {
            Optional<EntityLinkedItemStack> otherEli = this.getCapability(ApoliCapabilities.ENTITY_LINKED_ITEM_STACK).resolve();
            if (otherEli.isPresent() && otherEli.get().getEntity() != null) {
                eli.setEntity(otherEli.get().getEntity());
            }
        });
    }

    @ModifyReturnValue(method = "use", at = @At("RETURN"))
    private InteractionResultHolder<ItemStack> callActionOnUseInstantAfter(InteractionResultHolder<ItemStack> original, Level world, Player user, InteractionHand hand) {
        if (original.getResult().consumesAction() && ApoliAPI.getPowerContainer(user) != null && ApoliAPI.getPowerContainer(user).getPowers(ApoliPowers.ACTION_ON_ITEM_USE.get()).stream().anyMatch(p -> p.isBound() && p.value().getFactory().canRun(p, user, (ItemStack)(Object)this, this.getUseDuration() == 0 ? ActionOnItemUseConfiguration.TriggerType.INSTANT : ActionOnItemUseConfiguration.TriggerType.START, ActionOnItemUseConfiguration.PriorityPhase.AFTER))) {
            MutableObject<ItemStack> mutable = new MutableObject<>(original.getObject());
            ActionOnItemUsePower.execute(user, original.getObject(), mutable, this.getUseDuration() == 0 ? ActionOnItemUseConfiguration.TriggerType.INSTANT : ActionOnItemUseConfiguration.TriggerType.START, ActionOnItemUseConfiguration.PriorityPhase.AFTER);
            return (new InteractionResultHolder<>(original.getResult(), mutable.getValue()));
        }
        return original;
    }
}
