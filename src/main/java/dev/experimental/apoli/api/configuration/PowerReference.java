package dev.experimental.apoli.api.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.experimental.apoli.api.IDynamicFeatureConfiguration;
import dev.experimental.calio.api.CalioAPI;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record PowerReference(Identifier power) implements IDynamicFeatureConfiguration {
	public static Codec<PowerReference> codec(String fieldName) {
		return mapCodec(fieldName).codec();
	}

	public static MapCodec<PowerReference> mapCodec(String fieldName) {
		return SerializableDataTypes.IDENTIFIER.fieldOf(fieldName).xmap(PowerReference::new, PowerReference::power);
	}

	@Override
	public @NotNull List<String> getErrors(@NotNull MinecraftServer server) {
		return this.checkPower(CalioAPI.getDynamicRegistries(server), this.power).stream()
				.map(x -> "PowerReference/Missing Power: " + x.toString()).toList();
	}
}
