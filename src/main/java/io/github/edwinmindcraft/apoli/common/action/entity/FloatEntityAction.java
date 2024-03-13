package io.github.edwinmindcraft.apoli.common.action.entity;

import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public class FloatEntityAction extends EntityAction<FieldConfiguration<Float>> {

	public static FloatEntityAction ofLiving(BiConsumer<LivingEntity, Float> action, String field) {
		return new FloatEntityAction((e, i) -> {if (e instanceof LivingEntity le) action.accept(le, i);}, field);
	}

	public static FloatEntityAction ofPlayer(BiConsumer<Player, Float> action, String field) {
		return new FloatEntityAction((e, i) -> {if (e instanceof Player le) action.accept(le, i);}, field);
	}

	private final BiConsumer<Entity, Float> action;

	public FloatEntityAction(BiConsumer<Entity, Float> action, String field) {
		super(FieldConfiguration.codec(CalioCodecHelper.FLOAT, field));
		this.action = action;
	}

	@Override
	public void execute(FieldConfiguration<Float> configuration, Entity entity) {
		this.action.accept(entity, configuration.value());
	}
}
