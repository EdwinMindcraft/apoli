package io.github.apace100.apoli.power.factory.condition.entity;

import io.github.apace100.apoli.condition.configuration.ElytraFlightPossibleConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.caelus.api.CaelusApi;

public class ElytraFlightPossibleCondition extends EntityCondition<ElytraFlightPossibleConfiguration> {

	public ElytraFlightPossibleCondition() {
		super(ElytraFlightPossibleConfiguration.CODEC);
	}

	@Override
	public boolean check(@NotNull ElytraFlightPossibleConfiguration config, @NotNull Entity entity) {
		if (!(entity instanceof LivingEntity livingEntity))
			return false;
		boolean ability = config.checkAbility() || CaelusApi.getInstance().canFly(livingEntity);
		//FORGE STILL DOESN'T HAVE ELYTRA EVENTS.
		//(Also PRs are for 1.18.2 and that ain't gonna happen for quite a while)
		//I'm just going to assume that Caelus does all the hard work for me.
		//For when forge gets events (if ever):
		/*
		if (!ability && EntityElytraEvents.CUSTOM.invoker().useCustomElytra(livingEntity, false))
			ability = true;
		if (!EntityElytraEvents.ALLOW.invoker().allowElytraFlight(livingEntity))
			ability = false;
		*/
		boolean state = true;
		if (config.checkState())
			state = !livingEntity.onGround() && !livingEntity.isFallFlying() && !livingEntity.isInWater() && !livingEntity.hasEffect(MobEffects.LEVITATION);
		return ability && state;
	}

	/*public static ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(Apoli.identifier("elytra_flight_possible"),
				new SerializableData()
						.add("check_state", SerializableDataTypes.BOOLEAN, false)
						.add("check_ability", SerializableDataTypes.BOOLEAN, true),
				ElytraFlightPossibleCondition::condition
		);
	}*/
}