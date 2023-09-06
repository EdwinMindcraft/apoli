package io.github.edwinmindcraft.apoli.common.power;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.HolderConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.NonNullSupplier;

public class PreventBlockActionPower extends PowerFactory<HolderConfiguration<ConfiguredBlockCondition<?, ?>>> {
	public static boolean isSelectionPrevented(Entity entity, BlockPos pos, NonNullSupplier<BlockState> stateGetter) {
		return IPowerContainer.getPowers(entity, ApoliPowers.PREVENT_BLOCK_SELECTION.get()).stream().anyMatch(x -> x.value().getFactory().doesPrevent(x.value(), entity.level(), pos, stateGetter));
	}

	public static boolean isUsagePrevented(Entity entity, BlockPos pos) {
		return IPowerContainer.getPowers(entity, ApoliPowers.PREVENT_BLOCK_USAGE.get()).stream().anyMatch(x -> x.value().getFactory().doesPrevent(x.value(), entity.level(), pos, () -> entity.level().getBlockState(pos)));
	}

	public PreventBlockActionPower() {
		super(HolderConfiguration.optional(ConfiguredBlockCondition.optional("block_condition")));
	}

	public boolean doesPrevent(ConfiguredPower<HolderConfiguration<ConfiguredBlockCondition<?, ?>>, ?> configuration, LevelReader reader, BlockPos position, NonNullSupplier<BlockState> stateGetter) {
		return ConfiguredBlockCondition.check(configuration.getConfiguration().holder(), reader, position, stateGetter);
	}
}
