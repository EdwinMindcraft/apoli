package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.ApoliCommon;
import io.github.edwinmindcraft.calio.api.CalioAPI;
import io.github.edwinmindcraft.calio.api.registry.PlayerAbilities;
import net.minecraft.world.entity.Entity;

public class CreativeFlightPower extends PowerFactory<NoConfiguration> {
	public CreativeFlightPower() {
		super(NoConfiguration.CODEC);
		this.ticking(true);
	}

	@Override
	public void tick(ConfiguredPower<NoConfiguration, ?> configuration, Entity entity) {
		if (!entity.level().isClientSide) {
			boolean isActive = configuration.isActive(entity);
			boolean hasAbility = this.hasAbility(entity);
			if (isActive && !hasAbility)
				this.grantAbility(entity);
			else if (!isActive && hasAbility)
				this.revokeAbility(entity);
		}
	}

	@Override
	public void onGained(ConfiguredPower<NoConfiguration, ?> configuration, Entity entity) {
		if (!entity.level().isClientSide && configuration.isActive(entity) && !this.hasAbility(entity))
			this.grantAbility(entity);
	}

	@Override
	public void onLost(ConfiguredPower<NoConfiguration, ?> configuration, Entity entity) {
		if (!entity.level().isClientSide && this.hasAbility(entity))
			this.revokeAbility(entity);
	}

	public boolean hasAbility(Entity entity) {
		return CalioAPI.getAbilityHolder(entity).map(x -> x.has(PlayerAbilities.ALLOW_FLYING.get(), ApoliCommon.POWER_SOURCE)).orElse(false);
	}

	public void grantAbility(Entity entity) {
		CalioAPI.getAbilityHolder(entity).ifPresent(x -> x.grant(PlayerAbilities.ALLOW_FLYING.get(), ApoliCommon.POWER_SOURCE));
	}

	public void revokeAbility(Entity entity) {
		CalioAPI.getAbilityHolder(entity).ifPresent(x -> x.revoke(PlayerAbilities.ALLOW_FLYING.get(), ApoliCommon.POWER_SOURCE));
	}
}
