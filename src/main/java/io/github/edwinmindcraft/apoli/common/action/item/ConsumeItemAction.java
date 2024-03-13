package io.github.edwinmindcraft.apoli.common.action.item;

import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.ItemAction;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;

public class ConsumeItemAction extends ItemAction<FieldConfiguration<Integer>> {

	public ConsumeItemAction() {super(FieldConfiguration.codec(CalioCodecHelper.INT, "amount", 1));}

	@Override
	public void execute(FieldConfiguration<Integer> configuration, Level level, Mutable<ItemStack> stack) {
		stack.getValue().shrink(configuration.value());
	}
}
