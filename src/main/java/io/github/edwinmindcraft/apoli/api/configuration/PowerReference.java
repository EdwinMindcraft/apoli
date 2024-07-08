package io.github.edwinmindcraft.apoli.api.configuration;

import com.mojang.serialization.MapCodec;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record PowerReference(ResourceLocation power) implements IDynamicFeatureConfiguration {
	public static MapCodec<PowerReference> codec(String fieldName) {
		return SerializableDataTypes.IDENTIFIER.fieldOf(fieldName).xmap(PowerReference::new, PowerReference::power);
	}

	@Override
	public @NotNull List<String> getErrors(@NotNull RegistryAccess server) {
		return this.checkPower(server, this.power).stream()
				.map(x -> "PowerReference/Missing Power: " + x.toString()).toList();
	}
}
