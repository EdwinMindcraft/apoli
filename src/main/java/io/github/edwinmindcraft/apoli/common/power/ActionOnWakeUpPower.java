package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ActionOnWakeUpConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;

public class ActionOnWakeUpPower extends PowerFactory<ActionOnWakeUpConfiguration> {
	public static void execute(Entity player, BlockPos pos) {
		PowerContainer.getPowers(player, ApoliPowers.ACTION_ON_WAKE_UP.get()).stream()
				.filter(power -> power.value().getFactory().doesApply(power.value(), player, player.level(), pos))
				.forEach(aobbp -> aobbp.value().getFactory().executeActions(aobbp.value(), player, pos, Direction.DOWN));
	}

	public ActionOnWakeUpPower() {
		super(ActionOnWakeUpConfiguration.CODEC);
	}

	public boolean doesApply(ConfiguredPower<ActionOnWakeUpConfiguration, ?> config, Entity player, LevelReader reader, BlockPos position) {
		return ConfiguredBlockCondition.check(config.getConfiguration().blockCondition(), reader, position);
	}

	public void executeActions(ConfiguredPower<ActionOnWakeUpConfiguration, ?> config, Entity player, BlockPos pos, Direction dir) {
		ActionOnWakeUpConfiguration configuration = config.getConfiguration();
		ConfiguredBlockAction.execute(configuration.blockAction(), player.level(), pos, dir);
		ConfiguredEntityAction.execute(configuration.entityAction(), player);
	}
}
