package io.github.apace100.apoli.power.factory.action.bientity;

import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.DamageConfiguration;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DamageAction extends BiEntityAction<DamageConfiguration> {

	public DamageAction() {
		super(DamageConfiguration.CODEC);
	}

	@Override
	public void execute(@NotNull DamageConfiguration configuration, @NotNull Entity actor, @NotNull Entity target) {
		DamageSource providedSource = configuration.source();
		DamageSource source = new EntityDamageSource(providedSource.getMsgId(), actor);
		if (providedSource.isExplosion()) {
			source.setExplosion();
		}
		if (providedSource.isProjectile()) {
			source.setProjectile();
		}
		if (providedSource.isFall()) {
			source.setIsFall();
		}
		if (providedSource.isMagic()) {
			source.setMagic();
		}
		if (providedSource.isNoAggro()) {
			source.setNoAggro();
		}
		target.hurt(source, configuration.amount());
	}
}
