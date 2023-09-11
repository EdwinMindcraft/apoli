package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.SpawnEntityConfiguration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class SpawnEntityAction extends EntityAction<SpawnEntityConfiguration> {

	public SpawnEntityAction() {
		super(SpawnEntityConfiguration.CODEC);
	}

	@Override
	public void execute(SpawnEntityConfiguration configuration, Entity entity) {
		if (entity.level().isClientSide())
			return;
        ServerLevel serverWorld = (ServerLevel) entity.level();

        Optional<Entity> opt$entityToSpawn = MiscUtil.getEntityWithPassengers(
                serverWorld,
                configuration.type(),
                configuration.tag(),
                entity.position(),
                entity.getYRot(),
                entity.getXRot()
        );

        if (opt$entityToSpawn.isEmpty()) return;
        Entity entityToSpawn = opt$entityToSpawn.get();

        serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);
		ConfiguredEntityAction.execute(configuration.action(), entityToSpawn);
	}
}
