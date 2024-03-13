package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public class IntegerEntityAction extends EntityAction<FieldConfiguration<Integer>> {

	public static IntegerEntityAction ofLiving(BiConsumer<LivingEntity, Integer> action, String field) {
		return new IntegerEntityAction((e, i) -> {if (e instanceof LivingEntity le) action.accept(le, i);}, field);
	}

	public static IntegerEntityAction ofPlayer(BiConsumer<Player, Integer> action, String field) {
		return new IntegerEntityAction((e, i) -> {if (e instanceof Player le) action.accept(le, i);}, field);
	}

	private final BiConsumer<Entity, Integer> action;

	public IntegerEntityAction(BiConsumer<Entity, Integer> action, String field) {
		super(FieldConfiguration.codec(CalioCodecHelper.INT, field));
		this.action = action;
	}

	@Override
	public void execute(FieldConfiguration<Integer> configuration, Entity entity) {
		this.action.accept(entity, configuration.value());
	}
}
