package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.EnchantmentConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EnchantmentCondition extends EntityCondition<EnchantmentConfiguration> {

	public EnchantmentCondition() {
		super(EnchantmentConfiguration.CODEC);
	}

	@Override
	public boolean check(EnchantmentConfiguration configuration, Entity entity) {
		return entity instanceof LivingEntity living && configuration.enchantment().isPresent() && configuration.applyCheck(configuration.enchantment().get().getSlotItems(living).values());
	}
}
