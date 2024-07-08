package io.github.edwinmindcraft.apoli.api.power.configuration.power;

import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface IAttributeModifyingPowerConfiguration extends IDynamicFeatureConfiguration {
	ListConfiguration<AttributeModifier> modifiers();
	default void createModifiers(ResourceLocation powerId) {}
}
