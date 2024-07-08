package io.github.edwinmindcraft.apoli.common.condition.entity;

import com.mojang.authlib.GameProfile;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.apoli.common.condition.configuration.ScoreboardComparisonConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;

public class ScoreboardCondition extends EntityCondition<ScoreboardComparisonConfiguration> {

	public ScoreboardCondition() {
		super(ScoreboardComparisonConfiguration.CODEC);
	}

	@Override
	public boolean check(ScoreboardComparisonConfiguration configuration, Entity entity) {
		ScoreHolder scoreHolder = ScoreHolder.forNameOnly(entity.getScoreboardName());
		Scoreboard scoreboard = entity.level().getScoreboard();

		Objective objective = scoreboard.getObjective(configuration.objective());
		if (objective == null)
			return false;

		ScoreAccess access = scoreboard.getOrCreatePlayerScore(scoreHolder, objective);
		return configuration.comparison().check(access.get());
	}
}
