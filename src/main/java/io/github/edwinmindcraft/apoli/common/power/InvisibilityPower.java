package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.InvisibilityConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class InvisibilityPower extends PowerFactory<InvisibilityConfiguration> {

	public static boolean isArmorHidden(Entity player) {
		List<Holder<ConfiguredPower<InvisibilityConfiguration, InvisibilityPower>>> powers = PowerContainer.getPowers(player, ApoliPowers.INVISIBILITY.get());
		return !powers.isEmpty() && powers.stream().noneMatch(x -> x.value().getConfiguration().renderArmor());
	}

    public static boolean isOutlineHidden(Entity player) {
        List<Holder<ConfiguredPower<InvisibilityConfiguration, InvisibilityPower>>> powers = PowerContainer.getPowers(player, ApoliPowers.INVISIBILITY.get());
        return !powers.isEmpty() && powers.stream().noneMatch(x -> x.value().getConfiguration().renderOutline());
    }

	public InvisibilityPower() {
		super(InvisibilityConfiguration.CODEC);
	}
}
