package io.github.apace100.apoli.power.factory.action.bientity;

import io.github.apace100.apoli.util.MiscUtil;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.DamageConfiguration;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DamageAction extends BiEntityAction<DamageConfiguration> {

	public DamageAction() {
		super(DamageConfiguration.CODEC);
	}

	@Override
	public void execute(@NotNull DamageConfiguration configuration, @NotNull Entity actor, @NotNull Entity target) {
        DamageSource source = MiscUtil.createDamageSource(
                actor.damageSources(), configuration.source(), configuration.damageType(), actor);
		target.hurt(source, configuration.amount());
	}
}
