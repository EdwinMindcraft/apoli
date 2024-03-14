package io.github.edwinmindcraft.apoli.api.power;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.util.modifier.ModifierOperation;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;

import java.util.List;
import java.util.Optional;

/**
 * Information common to all modifiers.<br/>
 * Using this class is safe, that is no methods or field will
 * be removed from this class, and those will only be added.
 */
public record ModifierData(double value, Optional<Holder<ConfiguredPower<?, ?>>> resource, List<ConfiguredModifier<?>> modifiers) implements IDynamicFeatureConfiguration {
	public static final ModifierData DEFAULT = new ModifierData(0.0, Optional.empty(), ImmutableList.of());

	public static final MapCodec<ModifierData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			CalioCodecHelper.DOUBLE.fieldOf("value").forGetter(ModifierData::value),
			ExtraCodecs.strictOptionalField(ConfiguredPower.CODEC_SET.holderRef(), "resource").forGetter(ModifierData::resource),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.listOf(ConfiguredModifier.CODEC), "modifier", ImmutableList.of()).forGetter(ModifierData::modifiers)
	).apply(instance, ModifierData::new));
}
