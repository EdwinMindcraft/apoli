package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.GiveConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableObject;

public class GiveAction extends EntityAction<GiveConfiguration> {
	public GiveAction() {
		super(GiveConfiguration.CODEC);
	}

	@Override
	public void execute(GiveConfiguration configuration, Entity entity) {
		if (!entity.level().isClientSide()) {
			if (configuration.stack().isEmpty()) return;
			MutableObject<ItemStack> stack = new MutableObject<>(configuration.stack().copy());
			ConfiguredItemAction.execute(configuration.action(), entity.level(), stack);
			if (configuration.slot() != null && entity instanceof LivingEntity living) {
				ItemStack stackInSlot = living.getItemBySlot(configuration.slot());
				if (stackInSlot.isEmpty()) {
					living.setItemSlot(configuration.slot(), stack.getValue());
					return;
				} else if (ItemStack.isSameItem(stackInSlot, stack.getValue()) && stackInSlot.getCount() < stackInSlot.getMaxStackSize()) {
					int fit = Math.min(stackInSlot.getMaxStackSize() - stackInSlot.getCount(), stack.getValue().getCount());
					stackInSlot.grow(fit);
					stack.getValue().shrink(fit);
					if (stack.getValue().isEmpty()) {
						return;
					}
				}
			}
			if (entity instanceof Player player)
				player.getInventory().placeItemBackInInventory(stack.getValue());
			else
				entity.level().addFreshEntity(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), stack.getValue()));
		}
	}
}
