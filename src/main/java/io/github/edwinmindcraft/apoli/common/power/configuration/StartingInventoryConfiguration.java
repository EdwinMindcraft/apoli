package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;

public record StartingInventoryConfiguration(ListConfiguration<Tuple<Integer, ItemStack>> stacks,
											 boolean recurrent) implements IDynamicFeatureConfiguration {
	public static final Codec<StartingInventoryConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.mapCodec(ApoliDataTypes.POSITIONED_ITEM_STACK, "stack", "stacks").forGetter(StartingInventoryConfiguration::stacks),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "recurrent", false).forGetter(StartingInventoryConfiguration::recurrent)
	).apply(instance, StartingInventoryConfiguration::new));
}
