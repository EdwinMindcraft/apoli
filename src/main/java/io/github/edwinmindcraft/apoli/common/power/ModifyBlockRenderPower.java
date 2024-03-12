package io.github.edwinmindcraft.apoli.common.power;

import io.github.apace100.apoli.ApoliClient;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.configuration.ModifyBlockRenderConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;


public class ModifyBlockRenderPower extends PowerFactory<ModifyBlockRenderConfiguration> {

	public ModifyBlockRenderPower() {
		super(ModifyBlockRenderConfiguration.CODEC, false);
	}

	public boolean check(ConfiguredPower<ModifyBlockRenderConfiguration, ?> power, LevelReader world, BlockPos pos, Supplier<@NotNull BlockState> state) {
		return ConfiguredBlockCondition.check(power.getConfiguration().blockCondition(), world, pos, state);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void onAdded(ModifyBlockRenderConfiguration configuration, Entity entity) {
		ApoliClient.shouldReloadWorldRenderer = true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void onRemoved(ModifyBlockRenderConfiguration configuration, Entity entity) {
		ApoliClient.shouldReloadWorldRenderer = true;
	}
}
