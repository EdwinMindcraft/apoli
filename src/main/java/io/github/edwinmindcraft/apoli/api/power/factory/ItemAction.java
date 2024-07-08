package io.github.edwinmindcraft.apoli.api.power.factory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.IFactory;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;

public abstract class ItemAction<T extends IDynamicFeatureConfiguration> implements IFactory<T, ConfiguredItemAction<T, ?>, ItemAction<T>> {
	public static final Codec<ItemAction<?>> CODEC = ApoliRegistries.ITEM_ACTION.byNameCodec();
	private final MapCodec<ConfiguredItemAction<T, ?>> codec;

	protected ItemAction(MapCodec<T> codec) {
		this.codec = IFactory.singleCodec(codec, this::configure, ConfiguredItemAction::getConfiguration);
	}

	public MapCodec<ConfiguredItemAction<T, ?>> getCodec() {
		return this.codec;
	}

	@Override
	public final ConfiguredItemAction<T, ?> configure(T input) {
		return new ConfiguredItemAction<>(() -> this, input);
	}

	public abstract void execute(T configuration, Level level, Mutable<ItemStack> stack);
}
