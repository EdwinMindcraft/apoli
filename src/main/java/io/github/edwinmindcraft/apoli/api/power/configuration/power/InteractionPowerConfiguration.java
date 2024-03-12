package io.github.edwinmindcraft.apoli.api.power.configuration.power;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.VariableAccess;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Supplier;

public record InteractionPowerConfiguration(EnumSet<InteractionHand> hands, InteractionResult actionResult,
											Holder<ConfiguredItemCondition<?, ?>> itemCondition,
											Holder<ConfiguredItemAction<?, ?>> heldItemAction,
											@Nullable ItemStack itemResult,
											Holder<ConfiguredItemAction<?, ?>> resultItemAction) {
	public static InteractionResult reduce(InteractionResult first, InteractionResult second) {
		return second.consumesAction() && !first.consumesAction() || second.shouldSwing() && !first.shouldSwing() ? second : first;
	}

	public static final MapCodec<InteractionPowerConfiguration> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			CalioCodecHelper.optionalField(SerializableDataTypes.HAND_SET, "hands", (Supplier<EnumSet<InteractionHand>>) () -> EnumSet.allOf(InteractionHand.class)).forGetter(InteractionPowerConfiguration::hands),
			CalioCodecHelper.optionalField(SerializableDataTypes.ACTION_RESULT, "action_result", InteractionResult.SUCCESS).forGetter(InteractionPowerConfiguration::actionResult),
			ConfiguredItemCondition.optional("item_condition").forGetter(InteractionPowerConfiguration::itemCondition),
			ConfiguredItemAction.optional("held_item_action").forGetter(InteractionPowerConfiguration::heldItemAction),
			CalioCodecHelper.optionalField(SerializableDataTypes.ITEM_STACK, "result_stack").forGetter(x -> Optional.ofNullable(x.itemResult())),
			ConfiguredItemAction.optional("result_item_action").forGetter(InteractionPowerConfiguration::resultItemAction)
	).apply(instance, (t1, t2, t3, t4, t5, t6) -> new InteractionPowerConfiguration(t1, t2, t3, t4, t5.orElse(null), t6)));

	public static final MapCodec<InteractionPowerConfiguration> PREVENTING_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			CalioCodecHelper.optionalField(SerializableDataTypes.HAND_SET, "hands", (Supplier<EnumSet<InteractionHand>>) () -> EnumSet.allOf(InteractionHand.class)).forGetter(InteractionPowerConfiguration::hands),
			ConfiguredItemCondition.optional("item_condition").forGetter(InteractionPowerConfiguration::itemCondition),
			ConfiguredItemAction.optional("held_item_action").forGetter(InteractionPowerConfiguration::heldItemAction),
			CalioCodecHelper.optionalField(SerializableDataTypes.ITEM_STACK, "result_stack").forGetter(x -> Optional.ofNullable(x.itemResult())),
			ConfiguredItemAction.optional("result_item_action").forGetter(InteractionPowerConfiguration::resultItemAction)
	).apply(instance, (t1, t3, t4, t5, t6) -> new InteractionPowerConfiguration(t1, InteractionResult.FAIL, t3, t4, t5.orElse(null), t6)));

	public boolean appliesTo(Level level, InteractionHand hand, ItemStack stack) {
		return this.appliesTo(hand) && this.appliesTo(level, stack);
	}

	public boolean appliesTo(InteractionHand hand) {
		return this.hands().contains(hand);
	}

	public boolean appliesTo(Level level, ItemStack stack) {
		return ConfiguredItemCondition.check(this.itemCondition(), level, stack);
	}

	public void performActorItemStuff(LivingEntity actor, InteractionHand hand) {
		Mutable<ItemStack> heldStack = VariableAccess.hand(actor, hand);
		ConfiguredItemAction.execute(this.heldItemAction(), actor.level(), heldStack);
		Mutable<ItemStack> resultingStack = this.itemResult() == null ? heldStack : new MutableObject<>(this.itemResult().copy());
		boolean modified = this.itemResult() != null;
		if (this.resultItemAction().isBound()) {
			ConfiguredItemAction.execute(this.resultItemAction(), actor.level(), resultingStack);
		}
		if (modified) {
			if (resultingStack.getValue().isEmpty())
				actor.setItemInHand(hand, resultingStack.getValue());
			else if (actor instanceof Player player)
				player.getInventory().placeItemBackInInventory(resultingStack.getValue());
		}
	}
}
