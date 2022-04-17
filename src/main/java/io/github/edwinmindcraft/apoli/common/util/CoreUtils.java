package io.github.edwinmindcraft.apoli.common.util;

import io.github.apace100.apoli.access.ModifiableFoodEntity;
import io.github.apace100.apoli.util.AttributeUtil;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.ModifyFoodPower;
import io.github.edwinmindcraft.apoli.common.power.ModifyHarvestPower;
import io.github.edwinmindcraft.apoli.common.power.RestrictArmorPower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyFoodConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CoreUtils {
	/**
	 * Checks armor equipment conditions.
	 *
	 * @param entity The entity to check the conditions for.
	 * @param slot   The slot to check the item against.
	 * @param stack  The stack that is being placed.
	 *
	 * @return {@code true} if there exists a {@link ApoliPowers#RESTRICT_ARMOR} or {@link ApoliPowers#CONDITIONED_RESTRICT_ARMOR} that would prevent armor from being
	 * equipped, or if the item is an {@link Items#ELYTRA} and the player has {@link ApoliPowers#ELYTRA_FLIGHT}
	 */
	public static boolean isItemForbidden(Entity entity, EquipmentSlot slot, ItemStack stack) {
		if (!(entity instanceof LivingEntity living))
			return false;
		return RestrictArmorPower.isForbidden(living, slot, stack) ||
			   (stack.is(Items.ELYTRA) && IPowerContainer.hasPower(living, ApoliPowers.ELYTRA_FLIGHT.get()));
	}

	public static float modifyFriction(float friction, LevelReader level, BlockPos pos, @Nullable Entity entity, BlockState state) {
		if (entity != null)
			return IPowerContainer.modify(entity, ApoliPowers.MODIFY_SLIPPERINESS.get(), friction, p -> ConfiguredBlockCondition.check(p.getConfiguration().condition(), level, pos, () -> state));
		return friction;
	}

	public static int allowHarvest(BlockGetter level, BlockPos pos, Player player) {
		if (level instanceof LevelReader reader)
			return ModifyHarvestPower.isHarvestAllowed(player, reader, pos).map(x -> x ? 1 : 0).orElse(-1);
		return -1;
	}

	private static FoodProperties applyTranformations(FoodProperties original, List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> powers, Runnable syncAction) {
		if (powers.isEmpty()) return original;
		List<AttributeModifier> food = new LinkedList<>();
		List<AttributeModifier> saturation = new LinkedList<>();
		for (ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower> power : powers) {
			food.addAll(power.getConfiguration().foodModifiers().getContent());
			saturation.addAll(power.getConfiguration().saturationModifiers().getContent());
		}
		int originalNutrition = original == null ? 0 : original.getNutrition();
		float originalSaturation = original == null ? 0 : original.getSaturationModifier();
		int nutrition = (int) AttributeUtil.applyModifiers(food, originalNutrition);
		float saturationMod = (float) AttributeUtil.applyModifiers(saturation, originalSaturation);
		FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturationMod);
		if (nutrition != originalNutrition && nutrition == 0 || saturationMod != originalSaturation && saturationMod == 0)
			syncAction.run();
		if ((original != null && original.canAlwaysEat()) || powers.stream().anyMatch(x -> x.getConfiguration().alwaysEdible()))
			builder.alwaysEat();
		if (original != null) {
			if (powers.stream().noneMatch(x -> x.getConfiguration().preventEffects()))
				original.getEffects().forEach(pair -> builder.effect(pair::getFirst, pair.getSecond()));
			if (original.isFastFood())
				builder.fast();
			if (original.isMeat())
				builder.meat();
		}

		return builder.build();
	}

	public static FoodProperties transformFoodProperties(FoodProperties original, ItemStack stack, LivingEntity living) {
		if (living instanceof ModifiableFoodEntity mfe && mfe.getCurrentModifyFoodPowers() != null) {
			List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> powers = mfe.getCurrentModifyFoodPowers();
			return applyTranformations(original, powers, mfe::enforceFoodSync);
		}
		return original;
	}
}
