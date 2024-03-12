package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.TooltipConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class TooltipPower extends PowerFactory<TooltipConfiguration> {
	public static void tryAdd(@Nullable Entity entity, ItemStack itemStack, List<Component> tooltips) {
		if (entity == null) return;
		PowerContainer.getPowers(entity, ApoliPowers.TOOLTIP.get()).stream().sorted(Comparator.comparing(holder -> holder.value().getConfiguration().order())).forEachOrdered(power -> power.value().getFactory().tryAdd(power.value(), entity, itemStack, tooltips));
	}

	public TooltipPower() {
		super(TooltipConfiguration.CODEC);
	}

	public void tryAdd(ConfiguredPower<TooltipConfiguration, ?> config, Entity entity, ItemStack stack, List<Component> tooltips) {
		TooltipConfiguration configuration = config.getConfiguration();
		if (ConfiguredItemCondition.check(configuration.itemCondition(), entity.level(), stack))
			tooltips.addAll(configuration.components().entries());
	}
}
