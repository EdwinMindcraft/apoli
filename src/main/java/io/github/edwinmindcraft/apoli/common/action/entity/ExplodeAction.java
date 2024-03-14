package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.apace100.apoli.action.configuration.ExplodeConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ExplodeAction extends EntityAction<ExplodeConfiguration> {
	public ExplodeAction() {
		super(ExplodeConfiguration.CODEC);
	}

    @Override
    public void execute(@NotNull ExplodeConfiguration configuration, @NotNull Entity entity) {
        if (entity.level().isClientSide())
            return;
        ExplosionDamageCalculator calculator = !configuration.indestructible().is(ApoliDefaultConditions.BLOCK_DEFAULT.getId()) ? configuration.calculator() : null;
        explode(entity.level(), configuration.damageSelf() ? null : entity, entity.level().damageSources().explosion(null), calculator, entity.getX(), entity.getY(), entity.getZ(), configuration.power(), configuration.createFire(), configuration.destructionType());
    }

    private static void explode(Level world, @Nullable Entity entity, DamageSource damageSource, ExplosionDamageCalculator behavior, double x, double y, double z, float power, boolean createFire, Explosion.BlockInteraction destructionType) {
        Explosion explosion = new Explosion(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
        explosion.explode();
        explosion.finalizeExplosion(true);
    }

}
