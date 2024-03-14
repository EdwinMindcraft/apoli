package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.sounds.SoundEvent;

public record PlaySoundConfiguration(SoundEvent sound, float volume,
									 float pitch) implements IDynamicFeatureConfiguration {

	public static final Codec<PlaySoundConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.SOUND_EVENT.fieldOf("sound").forGetter(PlaySoundConfiguration::sound),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.FLOAT, "volume", 1F).forGetter(PlaySoundConfiguration::volume),
			ExtraCodecs.strictOptionalField(CalioCodecHelper.FLOAT, "pitch", 1F).forGetter(PlaySoundConfiguration::pitch)
	).apply(instance, PlaySoundConfiguration::new));
}
