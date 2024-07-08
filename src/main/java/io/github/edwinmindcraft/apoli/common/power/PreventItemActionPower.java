package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class PreventItemActionPower extends PowerFactory<FieldConfiguration<Optional<ConfiguredItemCondition<?, ?>>>> {

	public static boolean isUsagePrevented(Entity entity, ItemStack stack) {
		return PowerContainer.getPowers(entity, ApoliPowers.PREVENT_ITEM_USAGE.get()).stream().map(Holder::value).anyMatch(x -> x.getFactory().doesPrevent(x, entity.level(), stack));
	}

	public static List<ConfiguredPower<FieldConfiguration<Optional<ConfiguredItemCondition<?, ?>>>, PreventItemActionPower>> getPreventingForDisplay(Entity entity, ItemStack stack) {
		return PowerContainer.getPowers(entity, ApoliPowers.PREVENT_ITEM_USAGE.get()).stream().map(Holder::value).filter(x -> x.getFactory().doesPrevent(x, entity.level(), stack)).toList();
	}

	public PreventItemActionPower() {
		super(FieldConfiguration.optionalCodec(ConfiguredItemCondition.CODEC, "item_condition"));
	}

	public boolean doesPrevent(ConfiguredPower<FieldConfiguration<Optional<ConfiguredItemCondition<?, ?>>>, ?> configuration, Level level, ItemStack stack) {
		//FIXME Disable Food Restrictions.
		if (stack.has(DataComponents.FOOD) && !ApoliAPI.enableFoodRestrictions())
			return false;
		return configuration.getConfiguration().value().map(x -> x.check(level, stack)).orElse(true);
	}
}
