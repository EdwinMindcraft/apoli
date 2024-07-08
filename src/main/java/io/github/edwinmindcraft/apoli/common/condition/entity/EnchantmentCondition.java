package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.EnchantmentConfiguration;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public class EnchantmentCondition extends EntityCondition<EnchantmentConfiguration> {

	public EnchantmentCondition() {
		super(EnchantmentConfiguration.CODEC);
	}

	@Override
	public boolean check(EnchantmentConfiguration configuration, Entity entity) {
		if (!(entity instanceof LivingEntity living))
			return false;
		Iterable<ItemStack> stacks = configuration.enchantment().isPresent() ? entity.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getOrThrow(configuration.enchantment().get()).getSlotItems(living).values() : living.getAllSlots();
		return configuration.applyCheck(entity.registryAccess(), stacks);
	}
}
