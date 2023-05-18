package io.github.edwinmindcraft.apoli.api.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.ConfiguredCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.ItemCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public final class ConfiguredItemCondition<C extends IDynamicFeatureConfiguration, F extends ItemCondition<C>> extends ConfiguredCondition<C, F, ConfiguredItemCondition<?, ?>> {
	public static final Codec<ConfiguredItemCondition<?, ?>> CODEC = ItemCondition.CODEC.dispatch(ConfiguredItemCondition::getFactory, ItemCondition::getConditionCodec);
	public static final CodecSet<ConfiguredItemCondition<?, ?>> CODEC_SET = CalioCodecHelper.forDynamicRegistry(ApoliDynamicRegistries.CONFIGURED_ITEM_CONDITION_KEY, SerializableDataTypes.IDENTIFIER, CODEC);
	public static final Codec<Holder<ConfiguredItemCondition<?, ?>>> HOLDER = CODEC_SET.holder();

	public static MapCodec<Holder<ConfiguredItemCondition<?, ?>>> required(String name) {
		return HOLDER.fieldOf(name);
	}

	public static MapCodec<Holder<ConfiguredItemCondition<?, ?>>> optional(String name) {
		return CalioCodecHelper.registryDefaultedField(HOLDER, name, ApoliDynamicRegistries.CONFIGURED_ITEM_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_ITEM_CONDITIONS);
	}

	public static MapCodec<Holder<ConfiguredItemCondition<?, ?>>> optional(String name, ResourceKey<ConfiguredItemCondition<?, ?>> value) {
		return CalioCodecHelper.registryField(HOLDER, name, value, ApoliDynamicRegistries.CONFIGURED_ITEM_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_ITEM_CONDITIONS);
	}

	public static MapCodec<Holder<ConfiguredItemCondition<?, ?>>> optional(String name, ResourceLocation key) {
		return CalioCodecHelper.registryField(HOLDER, name, ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_ITEM_CONDITION_KEY, key), ApoliDynamicRegistries.CONFIGURED_ITEM_CONDITION_KEY, ApoliBuiltinRegistries.CONFIGURED_ITEM_CONDITIONS);
	}

	public static boolean check(Holder<ConfiguredItemCondition<?, ?>> condition, Level level, ItemStack stack) {
		return !condition.isBound() || condition.value().check(level, stack);
	}

	public ConfiguredItemCondition(Supplier<F> factory, C configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(Level level, ItemStack stack) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), level, stack);
	}

	@Override
	public String toString() {
		return "CIC:" + this.getFactory().getRegistryName() + "(" + this.getData() + ")-" + this.getConfiguration();
	}
}