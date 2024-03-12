package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.PreventSleepConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public class PreventSleepPower extends PowerFactory<PreventSleepConfiguration> {

	public static boolean tryPreventSleep(Player player, Level world, BlockPos pos) {
		boolean flag = false;
		for (Holder<ConfiguredPower<PreventSleepConfiguration, PreventSleepPower>> p : PowerContainer.getPowers(player, ApoliPowers.PREVENT_SLEEP.get())) {
			if (p.value().getFactory().doesPrevent(p.value(), world, pos)) {
				if (p.value().getConfiguration().allowSpawn() && player instanceof ServerPlayer spe)
					spe.setRespawnPosition(world.dimension(), pos, spe.getYRot(), false, true);
				player.displayClientMessage(Component.translatable(p.value().getConfiguration().message()), true);
				flag = true;
			}
		}
		return flag;
	}

	public PreventSleepPower() {
		super(PreventSleepConfiguration.CODEC);
	}

	public boolean doesPrevent(ConfiguredPower<PreventSleepConfiguration, ?> configuration, LevelReader reader, BlockPos pos) {
		return ConfiguredBlockCondition.check(configuration.getConfiguration().condition(), reader, pos);
	}
}
