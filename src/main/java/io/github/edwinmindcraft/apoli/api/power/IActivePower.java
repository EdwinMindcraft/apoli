package io.github.edwinmindcraft.apoli.api.power;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface IActivePower<T extends IDynamicFeatureConfiguration> {

	void activate(ConfiguredPower<T, ?> configuration, Entity player);

	Key getKey(ConfiguredPower<T, ?> configuration, @Nullable Entity player);

	record Key(String key, boolean continuous) {
		public static final Key PRIMARY = new Key("key.origins.primary_active", false);
		public static final Key SECONDARY = new Key("key.origins.secondary_active", false);

		public static final Codec<Key> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("key").forGetter(Key::key),
				CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "continuous", false).forGetter(Key::continuous)
		).apply(instance, Key::new));

		public static final Codec<Key> BACKWARD_COMPATIBLE_CODEC = Codec.either(CODEC, Codec.STRING).xmap(x -> x.map(Function.identity(), string -> switch (string) {
            case "secondary" -> SECONDARY;
            case "primary" -> PRIMARY;
            default -> new Key(string, false);
        }), Either::left);
	}
}
