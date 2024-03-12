package io.github.edwinmindcraft.apoli.common.util;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.access.IdentifiedLootTable;
import io.github.apace100.apoli.access.ModifiableFoodEntity;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.ModifyFoodPower;
import io.github.edwinmindcraft.apoli.common.power.ModifyHarvestPower;
import io.github.edwinmindcraft.apoli.common.power.ReplaceLootTablePower;
import io.github.edwinmindcraft.apoli.common.power.RestrictArmorPower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyFoodConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataResolver;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
			   (stack.is(Items.ELYTRA) && PowerContainer.hasPower(living, ApoliPowers.ELYTRA_FLIGHT.get()));
	}

	public static float modifyFriction(float friction, LevelReader level, BlockPos pos, @Nullable Entity entity, BlockState state) {
		if (entity != null)
			return PowerContainer.modify(entity, ApoliPowers.MODIFY_SLIPPERINESS.get(), friction, p -> ConfiguredBlockCondition.check(p.value().getConfiguration().condition(), level, pos, () -> state));
		return friction;
	}

	public static int allowHarvest(BlockGetter level, BlockPos pos, Player player) {
		if (level instanceof LevelReader reader)
			return ModifyHarvestPower.isHarvestAllowed(player, reader, pos).map(x -> x ? 1 : 0).orElse(-1);
		return -1;
	}

	private static FoodProperties applyTranformations(LivingEntity living, FoodProperties original, List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> powers, Runnable syncAction) {
		if (powers.isEmpty()) return original;
		List<ConfiguredModifier<?>> food = new LinkedList<>();
		List<ConfiguredModifier<?>> saturation = new LinkedList<>();
		for (ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower> power : powers) {
			food.addAll(power.getConfiguration().foodModifiers().getContent());
			saturation.addAll(power.getConfiguration().saturationModifiers().getContent());
		}
		int originalNutrition = original == null ? 0 : original.getNutrition();
		float originalSaturation = original == null ? 0 : original.getSaturationModifier();
		int nutrition = (int) ModifierUtil.applyModifiers(living, food, originalNutrition);
		float saturationMod = (float) ModifierUtil.applyModifiers(living, saturation, originalSaturation);
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
			return applyTranformations(living, original, powers, mfe::enforceFoodSync);
		}
		return original;
	}

    public static boolean shouldIgnoreWater(LivingEntity living, FluidState state) {
        return PowerContainer.hasPower(living, ApoliPowers.IGNORE_WATER.get()) && state.getFluidType() == ForgeMod.WATER_TYPE.get();
    }

    public static LootTable setLootTableId(LootTable original, LootDataResolver resolver, ResourceLocation id) {
        if (id.equals(ReplaceLootTablePower.REPLACED_TABLE_UTIL_ID)) {
            LootTable replace = ReplaceLootTablePower.peek();
            Apoli.LOGGER.info("Replacing " + id + " with " + ((IdentifiedLootTable)replace).getId());
            return replace;
            //cir.setReturnValue(getTable(ReplaceLootTablePower.LAST_REPLACED_TABLE_ID));
        } else {
            Optional<LootTable> tableOptional = resolver.getElementOptional(LootDataType.TABLE, id);
            if(tableOptional.isPresent()) {
                LootTable table = tableOptional.get();
                if(table instanceof IdentifiedLootTable identifiedLootTable) {
                    identifiedLootTable.setId(id, (LootDataManager)resolver);
                }
            }
        }
        return original;
    }
}
