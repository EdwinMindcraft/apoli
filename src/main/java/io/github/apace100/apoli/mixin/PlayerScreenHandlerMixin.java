package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ElytraFlightPower;
import io.github.apace100.apoli.power.RestrictArmorPower;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/screen/PlayerScreenHandler$1")
public abstract class PlayerScreenHandlerMixin extends Slot {

    public PlayerScreenHandlerMixin(Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Inject(method = "Lnet/minecraft/screen/PlayerScreenHandler$1;canInsert(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void preventArmorInsertion(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        Player player = ((Inventory)container).player;
        PowerHolderComponent component = PowerHolderComponent.KEY.get(player);
        EquipmentSlot slot = Mob.getEquipmentSlotForItem(stack);
        if(component.getPowers(RestrictArmorPower.class).stream().anyMatch(rap -> !rap.canEquip(stack, slot))) {
            info.setReturnValue(false);
        }
        if(PowerHolderComponent.getPowers(player, ElytraFlightPower.class).size() > 0) {
            if(stack.getItem() == Items.ELYTRA) {
                info.setReturnValue(false);
            }
        }
    }
}
