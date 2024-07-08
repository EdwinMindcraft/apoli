package io.github.edwinmindcraft.apoli.common.condition.entity;

import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;

import java.util.Objects;

public class GameModeCondition extends EntityCondition<FieldConfiguration<GameType>> {

	public GameModeCondition() {
		super(FieldConfiguration.codec(SerializableDataType.enumValue(GameType.class), "gamemode"));
	}

	protected boolean testClient(GameType mode, Entity entity) {
		return false;
	}

	@Override
	public boolean check(FieldConfiguration<GameType> configuration, Entity entity) {
		if (entity instanceof ServerPlayer) {
			ServerPlayerGameMode interactionMngr = ((ServerPlayer) entity).gameMode;
			return Objects.equals(interactionMngr.getGameModeForPlayer(), configuration.value());
		}
		return this.testClient(configuration.value(), entity);
	}

	public static class Client extends GameModeCondition {
		public Client() {
			super();
		}

		@Override
		protected boolean testClient(GameType mode, Entity entity) {
			return entity instanceof LocalPlayer && Minecraft.getInstance().gameMode != null && Objects.equals(Minecraft.getInstance().gameMode.getPlayerMode(), mode);
		}
	}
}
