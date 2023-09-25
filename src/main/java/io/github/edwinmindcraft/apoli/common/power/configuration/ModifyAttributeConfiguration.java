package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IValueModifyingPowerConfiguration;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class ModifyAttributeConfiguration implements IValueModifyingPowerConfiguration {

	public static final Codec<ModifyAttributeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.ATTRIBUTE.fieldOf("attribute").forGetter(ModifyAttributeConfiguration::attribute),
			ListConfiguration.MODIFIER_CODEC.forGetter(ModifyAttributeConfiguration::modifiers)
	).apply(instance, ModifyAttributeConfiguration::new));

	private final Attribute attribute;

	private final ListConfiguration<ConfiguredModifier<?>> modifiers;

	public ModifyAttributeConfiguration(Attribute attribute, ListConfiguration<ConfiguredModifier<?>> modifiers) {
		this.attribute = attribute;
		this.modifiers = modifiers;
	}

	public Attribute attribute() {
		return this.attribute;
	}

	@Override
	public ListConfiguration<ConfiguredModifier<?>> modifiers() {
		return this.modifiers;
	}
}
