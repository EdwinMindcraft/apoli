package io.github.edwinmindcraft.apoli.common.power;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.InteractionPowerConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.BiEntityInteractionConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Comparator;
import java.util.Optional;

public class ActionOnBeingUsedPower extends PowerFactory<BiEntityInteractionConfiguration> {
	public static Optional<InteractionResult> tryPrevent(Entity self, Entity other, InteractionHand hand) {
		for (Holder<ConfiguredPower<BiEntityInteractionConfiguration, ActionOnBeingUsedPower>> power : PowerContainer.getPowers(self, ApoliPowers.PREVENT_BEING_USED.get())) {
			Optional<InteractionResult> result = power.value().getFactory().tryExecute(power.value(), self, other, hand);
			if (result.isPresent())
				return result;
		}
		return Optional.empty();
	}

	public static Optional<InteractionResult> tryInteract(Entity self, Entity other, InteractionHand hand) {
		return PowerContainer.getPowers(self, ApoliPowers.ACTION_ON_BEING_USED.get()).stream().filter(p -> p.value().getConfiguration().priority() >= 0).sorted(Comparator.comparing(p -> p.value().getConfiguration().priority(), Comparator.reverseOrder())).flatMap(x -> x.value().getFactory().tryExecute(x.value(), self, other, hand).stream()).reduce(InteractionPowerConfiguration::reduce);
	}

	public ActionOnBeingUsedPower(Codec<BiEntityInteractionConfiguration> codec) {
		super(codec);
	}

	public Optional<InteractionResult> tryExecute(ConfiguredPower<BiEntityInteractionConfiguration, ?> configuration, Entity self, Entity other, InteractionHand hand) {
		if (other instanceof LivingEntity living && configuration.getConfiguration().check(other, self, hand, living.getItemInHand(hand))) {
			return Optional.of(configuration.getConfiguration().executeAction(other, self, hand));
		}
		return Optional.empty();
	}
}
