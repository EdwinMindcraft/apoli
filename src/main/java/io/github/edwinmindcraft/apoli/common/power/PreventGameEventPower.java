package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.PreventGameEventConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

public class PreventGameEventPower extends PowerFactory<PreventGameEventConfiguration> {
	public static boolean tryPreventGameEvent(Entity entity, GameEvent event) {
		List<ConfiguredPower<PreventGameEventConfiguration, PreventGameEventPower>> powers = PowerContainer.getPowers(entity, ApoliPowers.PREVENT_GAME_EVENT.get()).stream().map(Holder::value).filter(x -> x.getFactory().doesPrevent(x, event)).toList();
		powers.forEach(x -> x.getFactory().execute(x, entity));
		return !powers.isEmpty();
	}

	public PreventGameEventPower() {
		super(PreventGameEventConfiguration.CODEC);
	}

	public void execute(ConfiguredPower<PreventGameEventConfiguration, ?> configuration, Entity entity) {
		ConfiguredEntityAction.execute(configuration.getConfiguration().action(), entity);
	}

	public boolean doesPrevent(ConfiguredPower<PreventGameEventConfiguration, ?> configuration, GameEvent event) {
		return configuration.getConfiguration().doesPrevent(event);
	}
}
