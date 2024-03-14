package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ItemOnItemConfiguration(Holder<ConfiguredItemCondition<?, ?>> usingItemCondition,
									  Holder<ConfiguredItemCondition<?, ?>> onItemCondition,
									  int resultFromOnStack,
									  @Nullable ItemStack newStack,
									  Holder<ConfiguredItemAction<?, ?>> usingItemAction,
									  Holder<ConfiguredItemAction<?, ?>> onItemAction,
									  Holder<ConfiguredItemAction<?, ?>> resultItemAction,
									  Holder<ConfiguredEntityAction<?, ?>> entityAction) implements IDynamicFeatureConfiguration {
	public static final Codec<ItemOnItemConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredItemCondition.optional("using_item_condition").forGetter(ItemOnItemConfiguration::usingItemCondition),
			ConfiguredItemCondition.optional("on_item_condition").forGetter(ItemOnItemConfiguration::onItemCondition),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "result_from_on_stack", 0).forGetter(ItemOnItemConfiguration::resultFromOnStack),
			ExtraCodecs.strictOptionalField(SerializableDataTypes.ITEM_STACK, "result").forGetter(x -> Optional.ofNullable(x.newStack())),
			ConfiguredItemAction.optional("using_item_action").forGetter(ItemOnItemConfiguration::usingItemAction),
			ConfiguredItemAction.optional("on_item_action").forGetter(ItemOnItemConfiguration::onItemAction),
			ConfiguredItemAction.optional("result_item_action").forGetter(ItemOnItemConfiguration::resultItemAction),
			ConfiguredEntityAction.optional("entity_action").forGetter(ItemOnItemConfiguration::entityAction)
	).apply(instance, (t1, t2, t3, t4, t5, t6, t7, t8) -> new ItemOnItemConfiguration(
			t1,
			t2,
			t3,
			t4.orElse(null),
			t5,
			t6,
			t7,
			t8
	)));

	public boolean check(Level level, ItemStack using, ItemStack on) {
		return ConfiguredItemCondition.check(this.usingItemCondition(), level, using) && ConfiguredItemCondition.check(this.onItemCondition(), level, on);
	}

	public void execute(Entity entity, Mutable<ItemStack> using, Mutable<ItemStack> on, Slot slot) {
		Mutable<ItemStack> stack = new MutableObject<>(ItemStack.EMPTY);
		if (this.newStack() != null)
			stack.setValue(this.newStack().copy());
		else if (this.resultFromOnStack() > 0)
			stack.setValue(on.getValue().split(this.resultFromOnStack()));
		else
			stack.setValue(on.getValue());
		ConfiguredItemAction.execute(this.resultItemAction(), entity.level(), stack);
		ConfiguredItemAction.execute(this.usingItemAction(), entity.level(), using);
		ConfiguredItemAction.execute(this.onItemAction(), entity.level(), on);
		if (this.newStack() != null || this.resultItemAction().isBound()) {
			if (slot.getItem().isEmpty())
				slot.set(stack.getValue());
			else if (entity instanceof Player player)
				player.getInventory().placeItemBackInInventory(stack.getValue());
		}
		ConfiguredEntityAction.execute(this.entityAction(), entity);
	}
}
