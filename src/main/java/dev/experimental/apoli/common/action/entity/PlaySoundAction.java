package dev.experimental.apoli.common.action.entity;

import dev.experimental.apoli.common.action.configuration.PlaySoundConfiguration;
import dev.experimental.apoli.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;

public class PlaySoundAction extends EntityAction<PlaySoundConfiguration> {

	public PlaySoundAction() {
		super(PlaySoundConfiguration.CODEC);
	}

	@Override
	public void execute(PlaySoundConfiguration configuration, Entity entity) {
		if (entity instanceof PlayerEntity && configuration.sound() != null)
			entity.world.playSound(null, (entity).getX(), (entity).getY(), (entity).getZ(), configuration.sound(), SoundCategory.PLAYERS, configuration.volume(), configuration.pitch());
	}
}
