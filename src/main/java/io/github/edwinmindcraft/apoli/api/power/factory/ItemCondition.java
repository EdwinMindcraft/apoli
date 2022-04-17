package io.github.edwinmindcraft.apoli.api.power.factory;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.IConditionFactory;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

public abstract class ItemCondition<T extends IDynamicFeatureConfiguration> extends ForgeRegistryEntry<ItemCondition<?>> implements IConditionFactory<T, ConfiguredItemCondition<T, ?>, ItemCondition<T>> {
	public static final Codec<ItemCondition<?>> CODEC = ApoliRegistries.codec(ApoliRegistries.ITEM_CONDITION);
	private final Codec<ConfiguredItemCondition<T, ?>> codec;

	protected ItemCondition(Codec<T> codec) {
		this.codec = IConditionFactory.conditionCodec(codec, this);
	}

	@Override
	public Codec<ConfiguredItemCondition<T, ?>> getConditionCodec() {
		return this.codec;
	}

	@Override
	public final ConfiguredItemCondition<T, ?> configure(T input, ConditionData data) {
		return new ConfiguredItemCondition<>(() -> this, input, data);
	}

	protected boolean check(T configuration, @Nullable Level level, ItemStack stack) {
		return false;
	}

	public boolean check(T configuration, ConditionData data, @Nullable Level level, ItemStack stack) {
		return data.inverted() ^ this.check(configuration, level, stack);
	}
}
