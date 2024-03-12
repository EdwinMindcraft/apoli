package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ActionOnBlockBreakConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ActionOnBlockBreakPower extends PowerFactory<ActionOnBlockBreakConfiguration> {
	public static void execute(Entity player, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter, boolean successful) {
		PowerContainer.getPowers(player, ApoliPowers.ACTION_ON_BLOCK_BREAK.get()).stream()
				.filter(p -> p.value().getFactory().doesApply(p.value(), player, reader, position, stateGetter))
				.forEach(aobbp -> aobbp.value().getFactory().executeActions(aobbp.value(), player, successful, position, Direction.UP));
	}

	public ActionOnBlockBreakPower() {
		super(ActionOnBlockBreakConfiguration.CODEC);
	}

	public boolean doesApply(ConfiguredPower<ActionOnBlockBreakConfiguration, ?> config, Entity player, LevelReader reader, BlockPos position, Supplier<@NotNull BlockState> stateGetter) {
		return ConfiguredBlockCondition.check(config.getConfiguration().blockCondition(), reader, position, stateGetter);
	}

	public void executeActions(ConfiguredPower<ActionOnBlockBreakConfiguration, ?> config, Entity player, boolean successfulHarvest, BlockPos pos, Direction dir) {
		ActionOnBlockBreakConfiguration configuration = config.getConfiguration();
		if (successfulHarvest || !configuration.onlyWhenHarvested()) {
			ConfiguredBlockAction.execute(configuration.blockAction(), player.level(), pos, dir);
			ConfiguredEntityAction.execute(configuration.entityAction(), player);
		}
	}
}
