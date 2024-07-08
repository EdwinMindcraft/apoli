package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.Entity;

public class UsingEffectiveToolCondition extends EntityCondition<NoConfiguration> {

	public UsingEffectiveToolCondition() {
		super(NoConfiguration.CODEC);
	}

	protected boolean checkClient(Entity entity) {
		return false;
	}

	@Override
	public boolean check(NoConfiguration configuration, Entity entity) {
		if (entity instanceof ServerPlayer spe) {
			ServerPlayerGameMode interactionMngr = spe.gameMode;
			if (interactionMngr.isDestroyingBlock) {
				return spe.hasCorrectToolForDrops(entity.level().getBlockState(interactionMngr.destroyPos));
			}
		}
		return this.checkClient(entity);
	}

	public static class Client extends UsingEffectiveToolCondition {
		@Override
		protected boolean checkClient(Entity entity) {
			if (entity instanceof AbstractClientPlayer cpe) {
				MultiPlayerGameMode interactionMngr = Minecraft.getInstance().gameMode;
				if (interactionMngr != null && interactionMngr.isDestroying())
					return cpe.hasCorrectToolForDrops(entity.level().getBlockState(interactionMngr.destroyBlockPos));
			}
			return false;
		}
	}
}
