package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.*;
import io.github.edwinmindcraft.apoli.common.power.ModifyGrindstonePower;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;

import java.util.Optional;

public record ModifyGrindstoneConfiguration(Holder<ConfiguredItemCondition<?, ?>> topItemCondition, Holder<ConfiguredItemCondition<?, ?>> bottomItemCondition,
											Holder<ConfiguredItemCondition<?, ?>> outputItemCondition, Holder<ConfiguredBlockCondition<?, ?>> blockCondition,
											Optional<ItemStack> resultStack, Holder<ConfiguredItemAction<?, ?>> resultItemAction,
											Holder<ConfiguredItemAction<?, ?>> lateItemAction, Holder<ConfiguredEntityAction<?, ?>> entityAction,
											Holder<ConfiguredBlockAction<?, ?>> blockAction, ModifyGrindstonePower.ResultType resultType,
											Optional<ConfiguredModifier<?>> experienceModifier) implements IDynamicFeatureConfiguration {

	public static final Codec<ModifyGrindstoneConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredItemCondition.optional("top_condition").forGetter(ModifyGrindstoneConfiguration::topItemCondition),
			ConfiguredItemCondition.optional("bottom_condition").forGetter(ModifyGrindstoneConfiguration::bottomItemCondition),
			ConfiguredItemCondition.optional("output_condition").forGetter(ModifyGrindstoneConfiguration::outputItemCondition),
			ConfiguredBlockCondition.optional("block_condition").forGetter(ModifyGrindstoneConfiguration::blockCondition),
			ExtraCodecs.strictOptionalField(SerializableDataTypes.ITEM_STACK, "result_stack").forGetter(ModifyGrindstoneConfiguration::resultStack),
			ConfiguredItemAction.optional("item_action").forGetter(ModifyGrindstoneConfiguration::resultItemAction),
			ConfiguredItemAction.optional("item_action_after_grinding").forGetter(ModifyGrindstoneConfiguration::lateItemAction),
			ConfiguredEntityAction.optional("entity_action").forGetter(ModifyGrindstoneConfiguration::entityAction),
			ConfiguredBlockAction.optional("block_action").forGetter(ModifyGrindstoneConfiguration::blockAction),
			ExtraCodecs.strictOptionalField(SerializableDataType.enumValue(ModifyGrindstonePower.ResultType.class), "result_type", ModifyGrindstonePower.ResultType.UNCHANGED).forGetter(ModifyGrindstoneConfiguration::resultType),
			ExtraCodecs.strictOptionalField(ConfiguredModifier.CODEC, "xp_modifier").forGetter(ModifyGrindstoneConfiguration::experienceModifier)
	).apply(instance, ModifyGrindstoneConfiguration::new));


	public void tryExecute(ConfiguredPower<ModifyGrindstoneConfiguration, ModifyGrindstonePower> configuration, Entity entity, Mutable<ItemStack> itemStack, Optional<BlockPos> pos) {
		ConfiguredItemAction.execute(configuration.getConfiguration().lateItemAction(), entity.level(), itemStack);
		ConfiguredEntityAction.execute(configuration.getConfiguration().entityAction(), entity);
		pos.ifPresent(blockPos -> ConfiguredBlockAction.execute(configuration.getConfiguration().blockAction(), entity.level(), blockPos, Direction.UP));
	}

	public boolean doesApply(ConfiguredPower<ModifyGrindstoneConfiguration, ModifyGrindstonePower> configuration, Level level, ItemStack top, ItemStack bottom, ItemStack original, Optional<BlockPos> pos) {
		return ConfiguredItemCondition.check(configuration.getConfiguration().topItemCondition(), level, top) &&
				ConfiguredItemCondition.check(configuration.getConfiguration().bottomItemCondition(), level, bottom) &&
				ConfiguredItemCondition.check(configuration.getConfiguration().outputItemCondition(), level, original) &&
				(pos.isEmpty() || ConfiguredBlockCondition.check(configuration.getConfiguration().blockCondition(), level, pos.get()));
	}

}
