package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record PhasingConfiguration(Holder<ConfiguredBlockCondition<?,?>> phaseCondition, boolean blacklist,
								   RenderType renderType, float viewDistance,
								   Holder<ConfiguredEntityCondition<?,?>> phaseDownCondition) implements IDynamicFeatureConfiguration {
	public static final Codec<PhasingConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredBlockCondition.optional("block_condition").forGetter(PhasingConfiguration::phaseCondition),
			CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "blacklist", false).forGetter(PhasingConfiguration::blacklist),
			SerializableDataType.enumValue(RenderType.class).optionalFieldOf("render_type", RenderType.BLINDNESS).forGetter(PhasingConfiguration::renderType),
			CalioCodecHelper.optionalField(Codec.FLOAT, "view_distance", 10F).forGetter(PhasingConfiguration::viewDistance),
			ConfiguredEntityCondition.optional("phase_down_condition").forGetter(PhasingConfiguration::phaseDownCondition)
	).apply(instance, PhasingConfiguration::new));

	public boolean canPhaseDown(Entity entity) {
		return !this.phaseDownCondition().isBound() ? entity.isCrouching() : this.phaseDownCondition().value().check(entity);
	}

	public boolean canPhaseThrough(LevelReader reader, BlockPos pos, NonNullSupplier<BlockState> stateGetter) {
		return this.blacklist() != ConfiguredBlockCondition.check(this.phaseCondition(), reader, pos, stateGetter);
	}

	public enum RenderType {
		BLINDNESS, REMOVE_BLOCKS, NONE
	}
}
