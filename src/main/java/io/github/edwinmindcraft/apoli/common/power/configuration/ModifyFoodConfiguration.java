package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ModifyFoodConfiguration(ListConfiguration<ConfiguredModifier<?>> foodModifiers,
									  ListConfiguration<ConfiguredModifier<?>> saturationModifiers,
									  Holder<ConfiguredItemCondition<?, ?>> itemCondition,
									  Holder<ConfiguredEntityAction<?, ?>> entityAction,
									  @Nullable ItemStack replaceStack,
									  Holder<ConfiguredItemAction<?, ?>> itemAction,
									  boolean alwaysEdible,
									  boolean preventEffects) implements IDynamicFeatureConfiguration {
	public static final Codec<ModifyFoodConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.modifierCodec("food_modifier").forGetter(ModifyFoodConfiguration::foodModifiers),
			ListConfiguration.modifierCodec("saturation_modifier").forGetter(ModifyFoodConfiguration::saturationModifiers),
			ConfiguredItemCondition.optional("item_condition").forGetter(ModifyFoodConfiguration::itemCondition),
			ConfiguredEntityAction.optional("entity_action").forGetter(ModifyFoodConfiguration::entityAction),
			CalioCodecHelper.optionalField(SerializableDataTypes.ITEM_STACK, "replace_stack").forGetter(x -> Optional.ofNullable(x.replaceStack())),
			ConfiguredItemAction.optional("item_action").forGetter(ModifyFoodConfiguration::itemAction),
			CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "always_edible", false).forGetter(ModifyFoodConfiguration::alwaysEdible),
			CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "prevent_effects", false).forGetter(ModifyFoodConfiguration::preventEffects)
	).apply(instance, (t1, t2, t3, t4, t5, t6, t7, t8) -> new ModifyFoodConfiguration(t1, t2, t3, t4, t5.orElse(null), t6, t7, t8)));
}
