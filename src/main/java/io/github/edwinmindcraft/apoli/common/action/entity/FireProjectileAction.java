package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.apace100.apoli.util.MiscUtil;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.FireProjectileConfiguration;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.Random;

public class FireProjectileAction extends EntityAction<FireProjectileConfiguration> {

    public FireProjectileAction() {
        super(FireProjectileConfiguration.CODEC);
    }

    @Override
    public void execute(FireProjectileConfiguration configuration, Entity entity) {

        if (entity.level().isClientSide()) return;

        ServerLevel serverWorld = (ServerLevel) entity.level();

        for (int i = 0; i < configuration.count(); i++) {

            float yaw = entity.getYRot();
            float pitch = entity.getXRot();

            Optional<Entity> opt$entityToSpawn = MiscUtil.getEntityWithPassengers(
                    serverWorld,
                    configuration.entityType(),
                    configuration.tag().orElse(null),
                    entity.position().add(0, entity.getEyeHeight(entity.getPose()), 0),
                    yaw,
                    pitch
            );
            if (opt$entityToSpawn.isEmpty()) return;

            Vec3 rotationVector = entity.getLookAngle();
            Vec3 velocity = entity.getDeltaMovement();
            Entity entityToSpawn = opt$entityToSpawn.get();

            if (entityToSpawn instanceof Projectile projectileToSpawn) {

                if (projectileToSpawn instanceof AbstractHurtingProjectile ahp) {
                    ahp.xPower = rotationVector.x * configuration.speed();
                    ahp.yPower = rotationVector.y * configuration.speed();
                    ahp.zPower = rotationVector.z * configuration.speed();
                }

                projectileToSpawn.setOwner(entity);
                projectileToSpawn.shootFromRotation(entity, pitch, yaw, 0F, configuration.speed(), configuration.divergence());

            }

            else {

                float  j = 0.017453292F;
                double k = 0.007499999832361937D;

                float l = -Mth.sin(yaw * j) * Mth.cos(pitch * j);
                float m = -Mth.sin(pitch * j);
                float n =  Mth.cos(yaw * j) * Mth.cos(pitch * j);

                RandomSource random = serverWorld.getRandom();
                Vec3 vec3d = new Vec3(l, m, n)
                        .normalize()
                        .add(random.nextGaussian() * k * configuration.divergence(), random.nextGaussian() * k * configuration.divergence(), random.nextGaussian() * k * configuration.divergence())
                        .scale(configuration.speed());

                entityToSpawn.setDeltaMovement(vec3d);
                entityToSpawn.setDeltaMovement(entity.getDeltaMovement().add(velocity.x, entity.onGround() ? 0.0D : velocity.y, velocity.z));

            }

            serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);
            ConfiguredEntityAction.execute(configuration.projectileAction(), entityToSpawn);

        }
    }
}
