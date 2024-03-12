package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.power.ValueModifyingPowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyHarvestConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;

import java.util.Optional;

public class ModifyHarvestPower extends ValueModifyingPowerFactory<ModifyHarvestConfiguration> {
	/**
	 * This implementation assumes that if a single power says you can harvest, you can harvest.
	 */
	public static Optional<Boolean> isHarvestAllowed(Player player, LevelReader reader, BlockPos pos) {
		return PowerContainer.getPowers(player, ApoliPowers.MODIFY_HARVEST.get()).stream()
				.filter(x -> x.value().getFactory().doesApply(x.value(), reader, pos))
				.map(x -> x.value().getFactory().isHarvestAllowed(x.value()))
				.reduce((x, y) -> x || y);
	}

	public ModifyHarvestPower() {
		super(ModifyHarvestConfiguration.CODEC);
	}

	public boolean doesApply(ConfiguredPower<ModifyHarvestConfiguration, ?> config, LevelReader reader, BlockPos pos) {
		return ConfiguredBlockCondition.check(config.getConfiguration().condition(), reader, pos);
	}

	public boolean isHarvestAllowed(ConfiguredPower<ModifyHarvestConfiguration, ?> config) {
		return config.getConfiguration().allow();
	}
}
