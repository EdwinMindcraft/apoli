package io.github.apace100.apoli.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record AttributedEntityAttributeModifier(Attribute attribute, AttributeModifier modifier) {
	public static final Codec<AttributedEntityAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.ATTRIBUTE.fieldOf("attribute").forGetter(AttributedEntityAttributeModifier::attribute),
			SerializableDataTypes.MODIFIER_OPERATION.fieldOf("operation").forGetter(x -> x.modifier().getOperation()),
			CalioCodecHelper.DOUBLE.fieldOf("value").forGetter(x -> x.modifier().getAmount()),
			ExtraCodecs.strictOptionalField(Codec.STRING, "name", "Unnamed EntityAttributeModifier").forGetter(x -> x.modifier().getName())
	).apply(instance, (attribute, operation, value, name) -> new AttributedEntityAttributeModifier(attribute, new AttributeModifier(name, value, operation))));

	public AttributeModifier getModifier() {
		return this.modifier;
	}

	public Attribute getAttribute() {
		return this.attribute;
	}
}
