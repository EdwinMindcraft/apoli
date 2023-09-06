package io.github.apace100.apoli.mixin.forge;

import io.github.apace100.apoli.access.PowerCraftingInventory;
import io.github.apace100.apoli.util.ModifiedCraftingRecipe;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyCraftingConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(InventoryMenu.class)
public class InventoryMenuMixin {
    @Shadow @Final
    public Player owner;

    @Shadow @Final private CraftingContainer craftSlots;

    /*
    This has been moved here from CraftingResultSlotMixin, so we are able to modify the itemstack.
     */
    @ModifyVariable(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"), ordinal = 1)
    private ItemStack modifyOutputItems(ItemStack stack) {
        if (this.craftSlots instanceof TransientCraftingContainer craftingContainer && !this.owner.level().isClientSide && this.craftSlots instanceof PowerCraftingInventory pci && pci.getPower() != null && pci.getPower().getConfiguration() instanceof ModifyCraftingConfiguration config) {
            Optional<BlockPos> blockPos = ModifiedCraftingRecipe.getBlockFromInventory(craftingContainer);
            Mutable<ItemStack> newStack = new MutableObject<>(stack);
            config.execute(this.owner, blockPos.orElse(null));
            config.executeAfterCraftingAction(this.owner.level(), newStack);
            return newStack.getValue();
        }
        return stack;
    }
}
