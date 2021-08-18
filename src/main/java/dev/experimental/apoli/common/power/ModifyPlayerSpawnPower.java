package dev.experimental.apoli.common.power;

import dev.experimental.apoli.api.power.configuration.ConfiguredPower;
import dev.experimental.apoli.api.power.factory.PowerFactory;
import dev.experimental.apoli.common.power.configuration.ModifyPlayerSpawnConfiguration;
import io.github.apace100.apoli.Apoli;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;

public class ModifyPlayerSpawnPower extends PowerFactory<ModifyPlayerSpawnConfiguration> {

	public ModifyPlayerSpawnPower() {
		super(ModifyPlayerSpawnConfiguration.CODEC);
	}

	public void teleportToModifiedSpawn(ModifyPlayerSpawnConfiguration configuration, LivingEntity entity) {
		if (entity instanceof ServerPlayer serverPlayer) {
			Tuple<ServerLevel, BlockPos> spawn = configuration.getSpawn(entity, false);
			if (spawn != null) {
				Vec3 tpPos = DismountHelper.findSafeDismountLocation(EntityType.PLAYER, spawn.getA(), spawn.getB(), true);
				if (tpPos != null) {
					serverPlayer.teleportTo(spawn.getA(), tpPos.x, tpPos.y, tpPos.z, entity.getXRot(), entity.getYRot());
				} else {
					serverPlayer.teleportTo(spawn.getA(), spawn.getB().getX(), spawn.getB().getY(), spawn.getB().getZ(), entity.getXRot(), entity.getYRot());
					Apoli.LOGGER.warn("Could not spawn player with `ModifySpawnPower` at the desired location.");
				}
			}
		}
	}

	@Override
	public void onRemoved(ConfiguredPower<ModifyPlayerSpawnConfiguration, ?> configuration, LivingEntity player) {
		if (player instanceof ServerPlayer serverPlayer) {
			if (serverPlayer.getRespawnPosition() != null && serverPlayer.isRespawnForced())
				serverPlayer.setRespawnPosition(Level.OVERWORLD, null, 0F, false, false);
		}
	}

	@Override
	protected void onRespawn(ModifyPlayerSpawnConfiguration configuration, LivingEntity entity) {
		if (entity instanceof ServerPlayer player && !player.isRespawnForced())
			this.teleportToModifiedSpawn(configuration, entity);
	}
}

