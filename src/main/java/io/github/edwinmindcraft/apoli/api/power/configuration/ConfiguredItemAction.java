package io.github.edwinmindcraft.apoli.api.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.ConfiguredFactory;
import io.github.edwinmindcraft.apoli.api.power.factory.ItemAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.network.CodecSet;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;

import java.util.function.Supplier;

public final class ConfiguredItemAction<C extends IDynamicFeatureConfiguration, F extends ItemAction<C>> extends ConfiguredFactory<C, F, ConfiguredItemAction<?, ?>> {
	public static final Codec<ConfiguredItemAction<?, ?>> CODEC = ItemAction.CODEC.dispatch(ConfiguredItemAction::getFactory, ItemAction::getCodec);
	public static final CodecSet<ConfiguredItemAction<?, ?>> CODEC_SET = CalioCodecHelper.forDynamicRegistry(ApoliDynamicRegistries.CONFIGURED_ITEM_ACTION_KEY, SerializableDataTypes.IDENTIFIER, CODEC);
	public static final Codec<Holder<ConfiguredItemAction<?, ?>>> HOLDER = CODEC_SET.holder();

	public static MapCodec<Holder<ConfiguredItemAction<?, ?>>> required(String name) {
		return HOLDER.fieldOf(name);
	}

	public static MapCodec<Holder<ConfiguredItemAction<?, ?>>> optional(String name) {
		return CalioCodecHelper.registryDefaultedField(HOLDER, name, ApoliDynamicRegistries.CONFIGURED_ITEM_ACTION_KEY, ApoliBuiltinRegistries.CONFIGURED_ITEM_ACTIONS);
	}

	public static void execute(Holder<ConfiguredItemAction<?, ?>> action, Level level, Mutable<ItemStack> stack) {
		if (action.isBound())
			action.value().execute(level, stack);
	}

	public ConfiguredItemAction(Supplier<F> factory, C configuration) {
		super(factory, configuration);
	}

	public void execute(Level level, Mutable<ItemStack> stack) {
		this.getFactory().execute(this.getConfiguration(), level, stack);
	}

	@Override
	public String toString() {
		return "CIA:" + ApoliRegistries.ITEM_ACTION.getKey(this.getFactory()) + "-" + this.getConfiguration();
	}
}
