package io.github.edwinmindcraft.apoli.common.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.IntegerComparisonConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record LightLevelConfiguration(IntegerComparisonConfiguration comparison,
									  @Nullable LightLayer type) implements IDynamicFeatureConfiguration {
	public static final Codec<LightLevelConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			IntegerComparisonConfiguration.MAP_CODEC.forGetter(LightLevelConfiguration::comparison),
			ExtraCodecs.strictOptionalField(SerializableDataType.enumValue(LightLayer.class), "light_type").forGetter(x -> Optional.ofNullable(x.type()))
	).apply(instance, (comparison, lightType) -> new LightLevelConfiguration(comparison, lightType.orElse(null))));

	public int getLightLevel(LevelReader world, BlockPos pos) {
		return this.type() == null ? world.getMaxLocalRawBrightness(pos) : world.getBrightness(this.type(), pos);
	}
}
