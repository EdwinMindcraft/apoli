package io.github.edwinmindcraft.apoli.common.registry;

import io.github.apace100.apoli.util.ModifiedCraftingRecipe;
import io.github.apace100.apoli.util.PowerRestrictedCraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.RegistryObject;

import static io.github.edwinmindcraft.apoli.common.registry.ApoliRegisters.RECIPE_SERIALIZERS;

public class ApoliRecipeSerializers {
	public static final RegistryObject<RecipeSerializer<PowerRestrictedCraftingRecipe>> POWER_RESTRICTED = RECIPE_SERIALIZERS.register("power_restricted", () -> new SimpleCraftingRecipeSerializer<>(PowerRestrictedCraftingRecipe::new));
	public static final RegistryObject<RecipeSerializer<ModifiedCraftingRecipe>> MODIFIED = RECIPE_SERIALIZERS.register("modified", () -> new SimpleCraftingRecipeSerializer<>(ModifiedCraftingRecipe::new));

	public static void bootstrap() {}
}
