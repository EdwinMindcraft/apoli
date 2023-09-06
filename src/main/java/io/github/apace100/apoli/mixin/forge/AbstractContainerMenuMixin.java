package io.github.apace100.apoli.mixin.forge;

import io.github.apace100.apoli.access.PowerCraftingInventory;
import io.github.apace100.apoli.access.PowerModifiedGrindstone;
import io.github.apace100.apoli.util.ModifiedCraftingRecipe;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.ModifyGrindstonePower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyCraftingConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * This has been moved here from CraftingResultSlotMixin and GrindstoneScreenHandlerOutputSlotMixin, so we can actually modify the ItemStack.
 */
@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
	@Shadow
	@Final
	public NonNullList<Slot> slots;
	@Unique
	private int apoli$cachedMouseX;
	@Unique
	private Player apoli$cachedPlayer;

	@Inject(method = "doClick", at = @At("HEAD"))
	private void cacheMouseX(int pMouseX, int pMouseY, ClickType pClickType, Player pPlayer, CallbackInfo ci) {
		this.apoli$cachedMouseX = pMouseX;
		this.apoli$cachedPlayer = pPlayer;
	}

	@ModifyVariable(method = "doClick", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
	private Optional<ItemStack> handleGrindstoneLateActionsSet(Optional<ItemStack> original) {
		if (original.isPresent()) {
			if (((AbstractContainerMenu) (Object) this instanceof PowerModifiedGrindstone pmg) && apoli$cachedMouseX == 2) {
				Mutable<ItemStack> newStack = new MutableObject<>(original.get().copy());
				ModifyGrindstonePower.tryLateExecute(pmg.getAppliedPowers(), pmg.getPlayer(), newStack, pmg.getPos());
				return Optional.of(newStack.getValue());
			} else if (!this.apoli$cachedPlayer.level().isClientSide && this.slots.stream().anyMatch(slot -> slot instanceof ResultSlot)) {
				ResultSlot slot = (ResultSlot) this.slots.stream().filter(s -> s instanceof ResultSlot).findFirst().get();
                if (((ResultSlotAccessor) slot).getCraftSlots() instanceof TransientCraftingContainer craftingContainer) {
                    PowerCraftingInventory pci = (PowerCraftingInventory) ((ResultSlotAccessor) slot).getCraftSlots();
                    ConfiguredPower<?, ?> power = pci.getPower();
                    if (power != null && power.getConfiguration() instanceof ModifyCraftingConfiguration config) {
                        Optional<BlockPos> blockPos = ModifiedCraftingRecipe.getBlockFromInventory(craftingContainer);
                        config.execute(apoli$cachedPlayer, blockPos.orElse(null));
                        Mutable<ItemStack> newStack = new MutableObject<>(original.get().copy());
                        config.executeAfterCraftingAction(apoli$cachedPlayer.level(), newStack);
                        return Optional.of(newStack.getValue());
                    }
                }
			}
		}
		return original;
	}


	@ModifyVariable(method = "doClick", at = @At(value = "STORE", ordinal = 1), ordinal = 0)
	private Optional<ItemStack> handleGrindstoneLateActionsGrow(Optional<ItemStack> original) {
		if (original.isPresent() && ((AbstractContainerMenu) (Object) this instanceof PowerModifiedGrindstone pmg) && apoli$cachedMouseX == 2) {
			Mutable<ItemStack> newStack = new MutableObject<>(original.get().copy());
			ModifyGrindstonePower.tryLateExecute(pmg.getAppliedPowers(), pmg.getPlayer(), newStack, pmg.getPos());
			return Optional.of(newStack.getValue());
		} else if (!this.apoli$cachedPlayer.level().isClientSide && this.slots.stream().anyMatch(slot -> slot instanceof ResultSlot)) {
			ResultSlot slot = (ResultSlot) this.slots.stream().filter(s -> s instanceof ResultSlot).findFirst().get();
            if (((ResultSlotAccessor) slot).getCraftSlots() instanceof TransientCraftingContainer craftingContainer) {
                PowerCraftingInventory pci = (PowerCraftingInventory) ((ResultSlotAccessor) slot).getCraftSlots();
                ConfiguredPower<?, ?> power = pci.getPower();
                if (power != null && power.getConfiguration() instanceof ModifyCraftingConfiguration config) {
                    Optional<BlockPos> blockPos = ModifiedCraftingRecipe.getBlockFromInventory(craftingContainer);
                    config.execute(apoli$cachedPlayer, blockPos.orElse(null));
                    Mutable<ItemStack> newStack = new MutableObject<>(original.get().copy());
                    config.executeAfterCraftingAction(apoli$cachedPlayer.level(), newStack);
                    return Optional.of(newStack.getValue());
                }
            }
		}
		return original;
	}

	@ModifyVariable(method = "doClick", at = @At(value = "STORE", ordinal = 2), ordinal = 1)
	private ItemStack handleGrindstoneLateActionsSwap(ItemStack original) {
		if (((AbstractContainerMenu) (Object) this instanceof PowerModifiedGrindstone pmg) && apoli$cachedMouseX == 2) {
			Mutable<ItemStack> newStack = new MutableObject<>(original.copy());
			ModifyGrindstonePower.tryLateExecute(pmg.getAppliedPowers(), pmg.getPlayer(), newStack, pmg.getPos());
			return newStack.getValue();
		} else if (!this.apoli$cachedPlayer.level().isClientSide && this.slots.stream().anyMatch(slot -> slot instanceof ResultSlot)) {
			ResultSlot slot = (ResultSlot) this.slots.stream().filter(s -> s instanceof ResultSlot).findFirst().get();
            if (((ResultSlotAccessor) slot).getCraftSlots() instanceof TransientCraftingContainer craftingContainer) {
                PowerCraftingInventory pci = (PowerCraftingInventory) ((ResultSlotAccessor) slot).getCraftSlots();
                ConfiguredPower<?, ?> power = pci.getPower();
                if (power != null && power.getConfiguration() instanceof ModifyCraftingConfiguration config) {
                    Optional<BlockPos> blockPos = ModifiedCraftingRecipe.getBlockFromInventory(craftingContainer);
                    config.execute(apoli$cachedPlayer, blockPos.orElse(null));
                    Mutable<ItemStack> newStack = new MutableObject<>(original.copy());
                    config.executeAfterCraftingAction(apoli$cachedPlayer.level(), newStack);
                    return newStack.getValue();
                }
            }
		}
		return original;
	}

	@ModifyArg(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameTags(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 0), index = 0)
	private ItemStack checkForSameTagsWithNewContextInsert(ItemStack original) {
		if (((AbstractContainerMenu) (Object) this instanceof PowerModifiedGrindstone pmg) && apoli$cachedMouseX == 2) {
			Mutable<ItemStack> newStack = new MutableObject<>(original.copy());
			ModifyGrindstonePower.tryLateExecute(pmg.getAppliedPowers(), pmg.getPlayer(), newStack, pmg.getPos());
			return newStack.getValue();
		} else if (!this.apoli$cachedPlayer.level().isClientSide && this.slots.stream().anyMatch(slot -> slot instanceof ResultSlot)) {
			ResultSlot slot = (ResultSlot) this.slots.stream().filter(s -> s instanceof ResultSlot).findFirst().get();
            if (((ResultSlotAccessor) slot).getCraftSlots() instanceof TransientCraftingContainer craftingContainer) {
                PowerCraftingInventory pci = (PowerCraftingInventory) ((ResultSlotAccessor) slot).getCraftSlots();
                ConfiguredPower<?, ?> power = pci.getPower();
                if (power != null && power.getConfiguration() instanceof ModifyCraftingConfiguration config) {
                    Optional<BlockPos> blockPos = ModifiedCraftingRecipe.getBlockFromInventory(craftingContainer);
                    config.execute(apoli$cachedPlayer, blockPos.orElse(null));
                    Mutable<ItemStack> newStack = new MutableObject<>(original.copy());
                    config.executeAfterCraftingAction(apoli$cachedPlayer.level(), newStack);
                    return newStack.getValue();
                }
            }
		}
		return original;
	}

	@ModifyArg(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameTags(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 1), index = 0)
	private ItemStack checkForSameTagsWithNewContextGrow(ItemStack original) {
		if (((AbstractContainerMenu) (Object) this instanceof PowerModifiedGrindstone pmg) && apoli$cachedMouseX == 2) {
			Mutable<ItemStack> newStack = new MutableObject<>(original.copy());
			ModifyGrindstonePower.tryLateExecute(pmg.getAppliedPowers(), pmg.getPlayer(), newStack, pmg.getPos());
			return newStack.getValue();
		} else if (!this.apoli$cachedPlayer.level().isClientSide && this.slots.stream().anyMatch(slot -> slot instanceof ResultSlot)) {
			ResultSlot slot = (ResultSlot) this.slots.stream().filter(s -> s instanceof ResultSlot).findFirst().get();
            if (((ResultSlotAccessor) slot).getCraftSlots() instanceof TransientCraftingContainer craftingContainer) {
                PowerCraftingInventory pci = (PowerCraftingInventory) ((ResultSlotAccessor) slot).getCraftSlots();
                ConfiguredPower<?, ?> power = pci.getPower();
                if (power != null && power.getConfiguration() instanceof ModifyCraftingConfiguration config) {
                    Optional<BlockPos> blockPos = ModifiedCraftingRecipe.getBlockFromInventory(craftingContainer);
                    config.execute(apoli$cachedPlayer, blockPos.orElse(null));
                    Mutable<ItemStack> newStack = new MutableObject<>(original.copy());
                    config.executeAfterCraftingAction(apoli$cachedPlayer.level(), newStack);
                    return newStack.getValue();
                }
            }
		}
		return original;
	}
}
