package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.access.ModifiableFoodEntity;
import io.github.edwinmindcraft.apoli.common.power.ModifyFoodPower;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class HungerManagerMixin {

	@Shadow
	private int foodLevel;
	@Shadow
	private float saturationLevel;

	@Inject(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V", shift = At.Shift.AFTER))
	private void executeAdditionalEatAction(Item item, ItemStack stack, LivingEntity living, CallbackInfo ci) {
		if (living instanceof ModifiableFoodEntity mfe) {
			ModifyFoodPower.execute(mfe.getCurrentModifyFoodPowers(), living, living.level(), stack);
			if (mfe.shouldSyncFood() && !living.level().isClientSide() && living instanceof ServerPlayer sp) {
				sp.connection.send(new ClientboundSetHealthPacket(living.getHealth(), this.foodLevel, this.saturationLevel));
				mfe.resetFoodSync();
			}
		}
	}
}
