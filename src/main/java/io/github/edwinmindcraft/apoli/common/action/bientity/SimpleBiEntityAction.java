package io.github.edwinmindcraft.apoli.common.action.bientity;

import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityAction;
import io.github.edwinmindcraft.apoli.common.ApoliCommon;
import io.github.edwinmindcraft.apoli.common.network.S2CPlayerMount;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.BiConsumer;

public class SimpleBiEntityAction extends BiEntityAction<NoConfiguration> {

	public static void mount(Entity actor, Entity target) {
		actor.startRiding(target, true);
		if (!actor.level().isClientSide() && target instanceof ServerPlayer player) {
			ApoliCommon.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new S2CPlayerMount(actor.getId(), target.getId()));
		}
	}

	public static void setInLove(Entity actor, Entity target) {
		if (target instanceof Animal animal && actor instanceof Player player) animal.setInLove(player);
	}

	public static void tame(Entity actor, Entity target) {
		if (target instanceof TamableAnimal animal && actor instanceof Player player && !animal.isTame())
			animal.tame(player);
	}


	private final BiConsumer<Entity, Entity> action;

	public SimpleBiEntityAction(BiConsumer<Entity, Entity> action) {
		super(NoConfiguration.CODEC);
		this.action = action;
	}

	@Override
	public void execute(NoConfiguration configuration, Entity actor, Entity target) {
		this.action.accept(actor, target);
	}
}
