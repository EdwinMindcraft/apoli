package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.InteractionPowerConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public record BlockInteractionConfiguration(Holder<ConfiguredBlockCondition<?, ?>> blockCondition,
											Holder<ConfiguredBlockAction<?, ?>> blockAction,
											EnumSet<Direction> directions,
											Holder<ConfiguredEntityAction<?, ?>> entityAction,
											InteractionPowerConfiguration interaction) implements IDynamicFeatureConfiguration {

	public static final Codec<BlockInteractionConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredBlockCondition.optional("block_condition").forGetter(BlockInteractionConfiguration::blockCondition),
			ConfiguredBlockAction.optional("block_action").forGetter(BlockInteractionConfiguration::blockAction),
			SerializableDataTypes.DIRECTION_SET.optionalFieldOf("directions", EnumSet.allOf(Direction.class)).forGetter(BlockInteractionConfiguration::directions),
			ConfiguredEntityAction.optional("entity_action").forGetter(BlockInteractionConfiguration::entityAction),
			InteractionPowerConfiguration.MAP_CODEC.forGetter(BlockInteractionConfiguration::interaction)
	).apply(instance, BlockInteractionConfiguration::new));


	public boolean check(Level level, BlockPos blockPos, Direction direction, InteractionHand hand, ItemStack heldStack) {
		if (!this.interaction().appliesTo(level, hand, heldStack))
			return false;
		if (!this.directions().contains(direction)) {
			return false;
		}
		return ConfiguredBlockCondition.check(this.blockCondition(), level, blockPos);
	}

	public InteractionResult executeAction(Entity entity, BlockPos blockPos, Direction direction, InteractionHand hand) {
		ConfiguredBlockAction.execute(this.blockAction(), entity.level(), blockPos, direction);
		ConfiguredEntityAction.execute(this.entityAction(), entity);
		if (entity instanceof LivingEntity living)
			this.interaction().performActorItemStuff(living, hand);
		return this.interaction().actionResult();
	}
}
