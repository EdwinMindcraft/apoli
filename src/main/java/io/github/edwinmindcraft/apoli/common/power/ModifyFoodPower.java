package io.github.edwinmindcraft.apoli.common.power;

import io.github.apace100.apoli.util.AttributeUtil;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyFoodConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModifyFoodPower extends PowerFactory<ModifyFoodConfiguration> {

	public static List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> getValidPowers(Entity source, ItemStack stack) {
		return getValidPowers(source, source.level(), stack);
	}

	public static List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> getValidPowers(Entity source, Level level, ItemStack stack) {
		return PowerContainer.getPowers(source, ApoliPowers.MODIFY_FOOD.get()).stream().map(Holder::value)
				.filter(x -> x.getFactory().check(x, level, stack)).collect(Collectors.toList());
	}

	public static boolean isAlwaysEdible(Entity entity, Level level, ItemStack stack) {
		return getValidPowers(entity, level, stack).stream().anyMatch(x -> x.getConfiguration().alwaysEdible());
	}

	public static double apply(List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> source, Level level, ItemStack stack, double baseValue, Function<ModifyFoodConfiguration, ListConfiguration<AttributeModifier>> access) {
		List<AttributeModifier> modifiers = source.stream()
				.filter(x -> x.getFactory().check(x, level, stack))
				.flatMap(x -> access.apply(x.getConfiguration()).getContent().stream()).collect(Collectors.toList());
		return AttributeUtil.applyModifiers(modifiers, baseValue);
	}

	public static void modifyStack(Iterable<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> powers, Level level, Mutable<ItemStack> input) {
		for (ConfiguredPower<ModifyFoodConfiguration, ?> power : powers) {
			if (power.getConfiguration().replaceStack() != null)
				input.setValue(power.getConfiguration().replaceStack().copy());
			ConfiguredItemAction.execute(power.getConfiguration().itemAction(), level, input);
		}
	}

	public static void execute(List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> source, Entity entity, Level level, ItemStack stack) {
		source.stream()
				.filter(x -> x.getFactory().check(x, level, stack))
				.forEach(x -> x.getFactory().execute(x, entity));
	}

	public ModifyFoodPower() {
		super(ModifyFoodConfiguration.CODEC);
	}

	public boolean check(ConfiguredPower<ModifyFoodConfiguration, ?> config, Level level, ItemStack stack) {
		return ConfiguredItemCondition.check(config.getConfiguration().itemCondition(), level, stack);
	}

	public void execute(ConfiguredPower<ModifyFoodConfiguration, ?> config, Entity player) {
		ConfiguredEntityAction.execute(config.getConfiguration().entityAction(), player);
	}
}
