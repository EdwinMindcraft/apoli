package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

public record TooltipConfiguration(Holder<ConfiguredItemCondition<?, ?>> itemCondition,
								   int order,
								   ListConfiguration<Component> components) implements IDynamicFeatureConfiguration {
	public static final Codec<TooltipConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredItemCondition.optional("item_condition").forGetter(TooltipConfiguration::itemCondition),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "order", 0).forGetter(TooltipConfiguration::order),
			ListConfiguration.mapCodec(SerializableDataTypes.TEXT, "text", "texts").forGetter(TooltipConfiguration::components)
	).apply(instance, TooltipConfiguration::new));
}
