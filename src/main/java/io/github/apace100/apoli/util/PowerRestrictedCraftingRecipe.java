package io.github.apace100.apoli.util;

import com.google.common.collect.Lists;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import io.github.edwinmindcraft.apoli.common.registry.ApoliRecipeSerializers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class PowerRestrictedCraftingRecipe extends CustomRecipe {

	public PowerRestrictedCraftingRecipe(ResourceLocation id, CraftingBookCategory category) {
		super(id, category);
	}

	@Override
    public boolean matches(CraftingContainer inventory, Level world) {
        if (inventory instanceof TransientCraftingContainer craftingInventory) {
            return getRecipes(craftingInventory).stream().anyMatch(r -> r.matches(craftingInventory, world));
        }

        return false;
    }

    @Override
	public @NotNull ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
        if (inv instanceof TransientCraftingContainer craftingMenu) {
            Player player = this.getPlayerFromInventory(craftingMenu);
            if (player != null) {
                Optional<Recipe<CraftingContainer>> optional = this.getRecipes(craftingMenu).stream().filter(r -> r.matches(inv, player.level())).findFirst();
                if (optional.isPresent()) {
                    Recipe<CraftingContainer> recipe = optional.get();
                    return recipe.assemble(inv, access);
                }
            }
        }
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return ApoliRecipeSerializers.POWER_RESTRICTED.get();
	}

	private Player getPlayerFromInventory(TransientCraftingContainer inv) {
		return this.getPlayerFromHandler(inv.menu);
	}

	@SuppressWarnings("unchecked")
	private List<Recipe<CraftingContainer>> getRecipes(TransientCraftingContainer inv) {
		Player player = this.getPlayerFromHandler(inv.menu);
		if (player != null) {
			return IPowerContainer.getPowers(player, ApoliPowers.RECIPE.get()).stream().map(x -> (Recipe<CraftingContainer>) x.value().getConfiguration().value()).toList();
		}
		return Lists.newArrayList();
	}

	private Player getPlayerFromHandler(AbstractContainerMenu screenHandler) {
		if (screenHandler instanceof CraftingMenu menu) {
			return menu.player;
		}
		if (screenHandler instanceof InventoryMenu menu) {
			return menu.owner;
		}
		return null;
	}
}
