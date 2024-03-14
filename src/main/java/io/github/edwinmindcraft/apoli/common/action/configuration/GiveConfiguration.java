package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

public record GiveConfiguration(ItemStack stack, Holder<ConfiguredItemAction<?, ?>> action,
								@Nullable EquipmentSlot slot) implements IDynamicFeatureConfiguration {
	public static final Codec<GiveConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.ITEM_STACK.fieldOf("stack").forGetter(GiveConfiguration::stack),
			ConfiguredItemAction.optional("item_action").forGetter(GiveConfiguration::action),
			ExtraCodecs.strictOptionalField(SerializableDataTypes.EQUIPMENT_SLOT, "preferred_slot").forGetter(x -> Optional.ofNullable(x.slot()))
	).apply(instance, (t1, t2, t3) -> new GiveConfiguration(t1, t2, t3.orElse(null))));

	public GiveConfiguration(ItemStack stack) {
		this(stack, ApoliDefaultActions.ITEM_DEFAULT.getHolder().orElseThrow(), null);
	}
}
