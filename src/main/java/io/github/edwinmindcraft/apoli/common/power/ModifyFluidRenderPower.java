package io.github.edwinmindcraft.apoli.common.power;

import io.github.apace100.apoli.ApoliClient;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredFluidCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyFluidRenderConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class ModifyFluidRenderPower extends PowerFactory<ModifyFluidRenderConfiguration> {
	public ModifyFluidRenderPower() {
		super(ModifyFluidRenderConfiguration.CODEC, false);
	}

	public boolean check(ConfiguredPower<ModifyFluidRenderConfiguration, ?> power, LevelReader world, BlockPos pos, Supplier<@NotNull BlockState> state, FluidState fluid) {
		return ConfiguredBlockCondition.check(power.getConfiguration().blockCondition(), world, pos, state) && ConfiguredFluidCondition.check(power.getConfiguration().fluidCondition(), fluid);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void onAdded(ModifyFluidRenderConfiguration configuration, Entity entity) {
		ApoliClient.shouldReloadWorldRenderer = true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void onRemoved(ModifyFluidRenderConfiguration configuration, Entity entity) {
		ApoliClient.shouldReloadWorldRenderer = true;
	}
}
