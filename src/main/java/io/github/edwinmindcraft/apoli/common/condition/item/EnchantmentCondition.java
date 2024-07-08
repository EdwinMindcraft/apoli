package io.github.edwinmindcraft.apoli.common.condition.item;

import io.github.edwinmindcraft.apoli.api.power.factory.ItemCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.EnchantmentConfiguration;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EnchantmentCondition extends ItemCondition<EnchantmentConfiguration> {

	public EnchantmentCondition() {
		super(EnchantmentConfiguration.CODEC);
	}

	@Override
	public boolean check(EnchantmentConfiguration configuration, @Nullable Level level, ItemStack stack) {
		return configuration.applyCheck(level.registryAccess(), stack);
	}
}
