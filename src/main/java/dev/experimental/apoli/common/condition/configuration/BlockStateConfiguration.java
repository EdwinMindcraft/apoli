package dev.experimental.apoli.common.condition.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.experimental.apoli.api.IDynamicFeatureConfiguration;
import dev.experimental.apoli.api.configuration.IntegerComparisonConfiguration;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record BlockStateConfiguration(String property,
									  @Nullable IntegerComparisonConfiguration comparison,
									  @Nullable Boolean booleanValue,
									  @Nullable String stringValue) implements IDynamicFeatureConfiguration {
	public static final Codec<BlockStateConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("property").forGetter(BlockStateConfiguration::property),
			IntegerComparisonConfiguration.OPTIONAL_MAP_CODEC.forGetter(x -> Optional.ofNullable(x.comparison())),
			Codec.BOOL.optionalFieldOf("value").forGetter(x -> Optional.ofNullable(x.booleanValue())),
			Codec.STRING.optionalFieldOf("enum").forGetter(x -> Optional.ofNullable(x.stringValue()))
	).apply(instance, (t1, t2, t3, t4) -> new BlockStateConfiguration(t1, t2.orElse(null), t3.orElse(null), t4.orElse(null))));

	public boolean checkProperty(Object value) {
		boolean flag = false;
		if (this.stringValue() != null) {
			if (value instanceof Enum<?> enumValue)
				flag = enumValue.name().equalsIgnoreCase(this.stringValue());
			if (value instanceof StringRepresentable stringIdentifiable)
				flag |= stringIdentifiable.getSerializedName().equalsIgnoreCase(this.stringValue());
		}
		if (this.booleanValue() != null && value instanceof Boolean bool)
			return bool.booleanValue() == this.booleanValue();
		if (this.comparison() != null && value instanceof Integer intValue)
			return this.comparison().check(intValue);
		return flag;
	}

	@Override
	public @NotNull List<String> getErrors(@NotNull MinecraftServer server) {
		if (this.booleanValue() == null && this.stringValue() == null && this.comparison() == null)
			return ImmutableList.of("BlockState/No check were defined");
		return IDynamicFeatureConfiguration.super.getErrors(server);
	}
}
