package io.github.apace100.apoli.integration;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.EntityEvent;

import java.util.List;

public class ModifyValueEvent extends EntityEvent {
	private final PowerFactory<?> power;
	private final double baseValue;
	private final List<ConfiguredModifier<?>> modifiers;

	public ModifyValueEvent(Entity entity, PowerFactory<?> power, double baseValue, List<ConfiguredModifier<?>> modifiers) {
		super(entity);
		this.power = power;
		this.baseValue = baseValue;
		this.modifiers = modifiers;
	}

	public PowerFactory<?> getPower() {
		return this.power;
	}

	public double getBaseValue() {
		return this.baseValue;
	}

	public List<ConfiguredModifier<?>> getModifiers() {
		return this.modifiers;
	}

	public void addModifier(ConfiguredModifier<?> modifier) {
		this.modifiers.add(modifier);
	}
}
