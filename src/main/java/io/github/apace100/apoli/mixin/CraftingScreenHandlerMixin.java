package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.access.PowerCraftingInventory;
import io.github.apace100.apoli.util.ModifiedCraftingRecipe;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyCraftingConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CraftingMenu.class)
public class CraftingScreenHandlerMixin {

	@Shadow
	@Final
	public ContainerLevelAccess access;

	@Shadow @Final private CraftingContainer craftSlots;

	@Shadow @Final private Player player;

	@Inject(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
	private static void clearPowerCraftingInventory(AbstractContainerMenu menu, Level level, Player player, CraftingContainer container, ResultContainer result, CallbackInfo ci) {
		if (container instanceof PowerCraftingInventory)
			((PowerCraftingInventory) container).setPower(null);
	}

	/*
	This has been moved here from CraftingResultSlotMixin, so we are able to modify the itemstack.
	 */
	@ModifyVariable(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"), ordinal = 1)
	private ItemStack modifyOutputItems(ItemStack stack) {
		if (this.craftSlots instanceof TransientCraftingContainer craftingContainer && !this.player.level().isClientSide && this.craftSlots instanceof PowerCraftingInventory pci && pci.getPower() != null && pci.getPower().getConfiguration() instanceof ModifyCraftingConfiguration config) {
			Optional<BlockPos> blockPos = ModifiedCraftingRecipe.getBlockFromInventory(craftingContainer);
			Mutable<ItemStack> newStack = new MutableObject<>(stack);
			config.execute(player, blockPos.orElse(null));
			config.executeAfterCraftingAction(player.level(), newStack);
			return newStack.getValue();
		}
		return stack;
	}

	@Inject(method = "stillValid", at = @At("HEAD"), cancellable = true)
	private void allowUsingViaPower(Player player, CallbackInfoReturnable<Boolean> cir) {
		if (this.access.evaluate((world, pos) -> pos.equals(player.blockPosition()), false)) {
			cir.setReturnValue(true);
		}
	}
}
