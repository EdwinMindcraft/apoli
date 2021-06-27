package dev.experimental.apoli.common.power;

import dev.experimental.apoli.api.component.IPowerContainer;
import dev.experimental.apoli.api.power.ConfiguredFactory;
import dev.experimental.apoli.api.power.factory.PowerFactory;
import dev.experimental.apoli.common.power.configuration.ActionOnWakeUpConfiguration;
import dev.experimental.apoli.common.registry.ModPowers;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ActionOnWakeUpPower extends PowerFactory<ActionOnWakeUpConfiguration> {
	public static void execute(LivingEntity player, BlockPos pos) {
		IPowerContainer.getPowers(player, ModPowers.ACTION_ON_WAKE_UP.get()).stream()
				.filter(p -> p.getFactory().doesApply(p, player, pos))
				.forEach(aobbp -> aobbp.getFactory().executeActions(aobbp, player, pos, Direction.DOWN));
	}

	public ActionOnWakeUpPower() {
		super(ActionOnWakeUpConfiguration.CODEC);
	}

	public boolean doesApply(ConfiguredFactory<ActionOnWakeUpConfiguration, ?> config, LivingEntity player, BlockPos pos) {
		return this.doesApply(config, player, new CachedBlockPosition(player.world, pos, true));
	}

	public boolean doesApply(ConfiguredFactory<ActionOnWakeUpConfiguration, ?> config, LivingEntity player, CachedBlockPosition cbp) {
		return config.getConfiguration().blockCondition() == null || config.getConfiguration().blockCondition().check(cbp);
	}

	public void executeActions(ConfiguredFactory<ActionOnWakeUpConfiguration, ?> config, LivingEntity player, BlockPos pos, Direction dir) {
		ActionOnWakeUpConfiguration configuration = config.getConfiguration();
		if (configuration.blockAction() != null)
			configuration.blockAction().execute(player.world, pos, dir);
		if (configuration.entityAction() != null)
			configuration.entityAction().execute(player);
	}
}
