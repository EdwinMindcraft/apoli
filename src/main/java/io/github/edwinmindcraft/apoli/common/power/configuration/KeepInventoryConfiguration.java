package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

public record KeepInventoryConfiguration(Holder<ConfiguredItemCondition<?, ?>> keepCondition,
										 @Nullable Set<Integer> slots) implements IDynamicFeatureConfiguration {
	public static final Codec<KeepInventoryConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredItemCondition.optional("item_condition").forGetter(KeepInventoryConfiguration::keepCondition),
			ExtraCodecs.strictOptionalField(SerializableDataTypes.INTS, "slots").forGetter(x -> x.slots() == null ? Optional.empty() : Optional.of(new ArrayList<>(x.slots())))
	).apply(instance, (t1, t2) -> new KeepInventoryConfiguration(t1, t2.map(ImmutableSet::copyOf).orElse(null))));

	public boolean isApplicableTo(int slot, Level level, ItemStack stack) {
		if (this.slots() != null && !this.slots().contains(slot))
			return false;
		return ConfiguredItemCondition.check(this.keepCondition(), level, stack);
	}
}
