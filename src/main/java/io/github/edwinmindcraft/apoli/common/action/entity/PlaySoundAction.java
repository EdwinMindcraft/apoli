package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.common.action.configuration.PlaySoundConfiguration;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

public class PlaySoundAction extends EntityAction<PlaySoundConfiguration> {

	public PlaySoundAction() {
		super(PlaySoundConfiguration.CODEC);
	}

	@Override
	public void execute(PlaySoundConfiguration configuration, Entity entity) {
		SoundSource source = entity instanceof Player ? SoundSource.PLAYERS : entity instanceof Monster ? SoundSource.HOSTILE : SoundSource.NEUTRAL;
		entity.level().playSound(null, (entity).getX(), (entity).getY(), (entity).getZ(), configuration.sound(), source, configuration.volume(), configuration.pitch());
	}
}
