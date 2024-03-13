package io.github.edwinmindcraft.apoli.api.power;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;

/**
 * Information common to all condition types.<br/>
 * Using this class is safe, that is no methods or field will
 * be removed from this class, and those will only be added.
 */
public record ConditionData(boolean inverted) {
	public static final ConditionData DEFAULT = new ConditionData(false);

	public static final MapCodec<ConditionData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "inverted", false).forGetter(ConditionData::inverted)
	).apply(instance, ConditionData::new));
}
