package io.github.apace100.apoli.mixin;

import io.github.edwinmindcraft.apoli.common.power.ModifyFoodPower;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

	@Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResultHolder;fail(Ljava/lang/Object;)Lnet/minecraft/world/InteractionResultHolder;"), cancellable = true)
	private void tryItemAlwaysEdible(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		ItemStack itemStack = user.getItemInHand(hand);
		if (ModifyFoodPower.isAlwaysEdible(user, world, itemStack)) {
			user.startUsingItem(hand);
			cir.setReturnValue(InteractionResultHolder.consume(itemStack));
		}
	}
}
