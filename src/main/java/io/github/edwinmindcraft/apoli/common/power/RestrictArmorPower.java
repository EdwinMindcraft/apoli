package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.RestrictArmorConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Stream;

public class RestrictArmorPower extends PowerFactory<RestrictArmorConfiguration> {
	public static boolean isForbidden(Entity player, EquipmentSlot slot, ItemStack stack) {
		return Stream.concat(IPowerContainer.getPowers(player, ApoliPowers.CONDITIONED_RESTRICT_ARMOR.get()).stream(), IPowerContainer.getPowers(player, ApoliPowers.RESTRICT_ARMOR.get()).stream())
				.anyMatch(x -> x.getConfiguration().check(slot, player.level, stack));
	}

	public RestrictArmorPower() {
		super(RestrictArmorConfiguration.CODEC, false);
	}

	@Override
	protected void onGained(RestrictArmorConfiguration configuration, Entity player) {
		configuration.dropIllegalItems(player);
	}
}
