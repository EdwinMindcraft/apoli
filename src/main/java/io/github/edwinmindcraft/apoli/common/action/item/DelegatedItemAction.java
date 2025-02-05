package io.github.edwinmindcraft.apoli.common.action.item;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.power.factory.ItemAction;
import io.github.edwinmindcraft.apoli.common.action.meta.IDelegatedActionConfiguration;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.tuple.Pair;

public class DelegatedItemAction<T extends IDelegatedActionConfiguration<Pair<Level, Mutable<ItemStack>>>> extends ItemAction<T> {
	public DelegatedItemAction(Codec<T> codec) {
		super(codec);
	}

	@Override
	public void execute(T configuration, Level level, Mutable<ItemStack> stack) {
		configuration.execute(Pair.of(level, stack));
	}
}
