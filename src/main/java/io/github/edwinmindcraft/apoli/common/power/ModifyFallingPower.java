package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.factory.power.ValueModifyingPowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyFallingConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.neoforge.common.NeoForgeMod;

public class ModifyFallingPower extends ValueModifyingPowerFactory<ModifyFallingConfiguration> {

	public ModifyFallingPower() {
		super(ModifyFallingConfiguration.CODEC);
	}

	// As Origins Fabric now utilises special modifiers for this class, so we hook into the Forge Attribute by extending ModifyAttributePower.
	public static double apply(Entity entity, double originalValue) {
		if (!(entity instanceof LivingEntity living))
			return originalValue;
		AttributeInstance attribute = living.getAttribute(NeoForgeMod.ENTITY_GRAVITY.value());
		if (attribute != null) {
			double modifier = PowerContainer.modify(entity, ApoliPowers.MODIFY_FALLING.get(), originalValue);
			if (modifier != originalValue && modifier >= 0.0) {
				return modifier;
			}
		}
		return originalValue;
	}
}
