package io.github.apace100.apoli.power.factory.condition.block;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.LegacyMaterial;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.NotNull;

public class MaterialCondition extends BlockCondition<ListConfiguration<LegacyMaterial>> {

	public MaterialCondition() {
		super(ListConfiguration.codec(ApoliDataTypes.LEGACY_MATERIAL, "material", "materials"));
	}

	@Override
	protected boolean check(@NotNull ListConfiguration<LegacyMaterial> configuration, @NotNull LevelReader reader, @NotNull BlockPos position, @NotNull NonNullSupplier<BlockState> stateGetter) {
		return configuration.getContent().stream().anyMatch(lm -> lm.blockStateIsOfMaterial(stateGetter.get()));
	}
}
