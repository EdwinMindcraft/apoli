package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.world.entity.ai.attributes.Attribute;

public record AttributeModifyTransferConfiguration(PowerFactory<?> target, Attribute source,
												   double multiplier) implements IDynamicFeatureConfiguration {
	public static final Codec<AttributeModifyTransferConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PowerFactory.IGNORE_NAMESPACE_CODEC.fieldOf("class").forGetter(AttributeModifyTransferConfiguration::target),
			SerializableDataTypes.ATTRIBUTE.fieldOf("attribute").forGetter(AttributeModifyTransferConfiguration::source),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.DOUBLE, "multiplier", 1.0).forGetter(AttributeModifyTransferConfiguration::multiplier)
	).apply(instance, AttributeModifyTransferConfiguration::new));
}
