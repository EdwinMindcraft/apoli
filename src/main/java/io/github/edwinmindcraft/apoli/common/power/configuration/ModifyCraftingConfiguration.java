package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.OptionalFuncs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record ModifyCraftingConfiguration(@Nullable ResourceLocation recipeIdentifier,
										  Holder<ConfiguredItemCondition<?, ?>> itemCondition,
										  @Nullable ItemStack newStack,
										  Holder<ConfiguredItemAction<?, ?>> itemAction,
										  Holder<ConfiguredEntityAction<?, ?>> entityAction,
										  Holder<ConfiguredBlockAction<?, ?>> blockAction,
										  Holder<ConfiguredItemAction<?, ?>> afterCraftingItemAction) implements IDynamicFeatureConfiguration {
	public static final Codec<ModifyCraftingConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			CalioCodecHelper.optionalField(SerializableDataTypes.IDENTIFIER, "recipe").forGetter(OptionalFuncs.opt(ModifyCraftingConfiguration::recipeIdentifier)),
			ConfiguredItemCondition.optional("item_condition").forGetter(ModifyCraftingConfiguration::itemCondition),
			CalioCodecHelper.optionalField(SerializableDataTypes.ITEM_STACK, "result").forGetter(OptionalFuncs.opt(ModifyCraftingConfiguration::newStack)),
			ConfiguredItemAction.optional("item_action").forGetter(ModifyCraftingConfiguration::itemAction),
			ConfiguredEntityAction.optional("entity_action").forGetter(ModifyCraftingConfiguration::entityAction),
			ConfiguredBlockAction.optional("block_action").forGetter(ModifyCraftingConfiguration::blockAction),
			ConfiguredItemAction.optional("item_action_after_crafting").forGetter(ModifyCraftingConfiguration::afterCraftingItemAction)
	).apply(instance, (t1, t2, t3, t4, t5, t6, t7) -> new ModifyCraftingConfiguration(t1.orElse(null), t2, t3.orElse(null), t4, t5, t6, t7)));

	public boolean doesApply(CraftingContainer container, Recipe<? super CraftingContainer> recipe, Level level) {
		return (this.recipeIdentifier() == null || Objects.equals(recipe.getId(), this.recipeIdentifier())) &&
			   (!this.itemCondition().isBound() || ConfiguredItemCondition.check(this.itemCondition(), level, recipe.assemble(container, level.registryAccess())));
	}

	public ItemStack createResult(CraftingContainer container, Recipe<? super CraftingContainer> recipe, Level level) {
		Mutable<ItemStack> stack = new MutableObject<>();
		if (this.newStack() != null)
			stack.setValue(this.newStack().copy());
		else
			stack.setValue(recipe.assemble(container, level.registryAccess()));
		ConfiguredItemAction.execute(this.itemAction(), level, stack);
		return stack.getValue();
	}

	public void execute(Entity entity, @Nullable BlockPos pos) {
		if (pos != null && this.blockAction().isBound())
			ConfiguredBlockAction.execute(this.blockAction(), entity.level(), pos, Direction.UP);
		ConfiguredEntityAction.execute(this.entityAction(), entity);
	}

	public void executeAfterCraftingAction(Level level, Mutable<ItemStack> stack) {
		ConfiguredItemAction.execute(this.afterCraftingItemAction(), level, stack);
	}
}
