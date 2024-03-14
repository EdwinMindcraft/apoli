package io.github.apace100.apoli.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ModifyBlockStateConfiguration(String property, ResourceOperation operation, @Nullable Integer change,
											@Nullable Boolean value, @Nullable String enumValue,
											boolean cycle) implements IDynamicFeatureConfiguration {
	public static final Codec<ModifyBlockStateConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("property").forGetter(ModifyBlockStateConfiguration::property),
			ExtraCodecs.strictOptionalField(ApoliDataTypes.RESOURCE_OPERATION, "operation", ResourceOperation.ADD).forGetter(ModifyBlockStateConfiguration::operation),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "change").forGetter(x -> Optional.ofNullable(x.change())),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "value").forGetter(x -> Optional.ofNullable(x.value())),
			ExtraCodecs.strictOptionalField(Codec.STRING, "enum").forGetter(x -> Optional.ofNullable(x.enumValue())),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "change", false).forGetter(ModifyBlockStateConfiguration::cycle)
	).apply(instance, (t1, t2, t3, t4, t5, t6) -> new ModifyBlockStateConfiguration(t1, t2, t3.orElse(null), t4.orElse(null), t5.orElse(null), t6)));
}
