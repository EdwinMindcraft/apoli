package io.github.edwinmindcraft.apoli.common.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.DamageSourceDescription;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record DamageOverTimeConfiguration(int interval, int delay, float damageEasy,
										  float damage, ResourceKey<DamageType> damageType,
                                          @Nullable DamageSourceDescription damageSource,
										  @Nullable Enchantment protectionEnchantment,
										  float protectionEffectiveness) implements IDynamicFeatureConfiguration {

	public static final Codec<DamageOverTimeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			CalioCodecHelper.optionalField(CalioCodecHelper.INT, "interval", 20).forGetter(DamageOverTimeConfiguration::interval),
			CalioCodecHelper.optionalField(CalioCodecHelper.INT, "onset_delay").forGetter(x -> x.delay() == x.interval() ? Optional.empty() : Optional.of(x.delay())),
			CalioCodecHelper.optionalField(CalioCodecHelper.FLOAT, "damage_easy").forGetter(x -> x.damageEasy() == x.damage() ? Optional.empty() : Optional.of(x.damageEasy())),
			CalioCodecHelper.FLOAT.fieldOf("damage").forGetter(DamageOverTimeConfiguration::damage),
            CalioCodecHelper.optionalField(SerializableDataTypes.DAMAGE_TYPE, "damage_type", DamageTypes.GENERIC).forGetter(DamageOverTimeConfiguration::damageType),
			CalioCodecHelper.optionalField(ApoliDataTypes.DAMAGE_SOURCE_DESCRIPTION, "damage_source").forGetter(x -> Optional.ofNullable(x.damageSource())),
			CalioCodecHelper.optionalField(SerializableDataTypes.ENCHANTMENT, "protection_enchantment").forGetter(x -> Optional.ofNullable(x.protectionEnchantment())),
			CalioCodecHelper.optionalField(CalioCodecHelper.FLOAT, "protection_effectiveness", 1.0F).forGetter(DamageOverTimeConfiguration::protectionEffectiveness)
	).apply(instance, (t1, t2, t3, t4, t5, t6, t7, t8) -> new DamageOverTimeConfiguration(t1, t2.orElse(t1), t3.orElse(t4), t4, t5, t6.orElse(null), t7.orElse(null), t8)));
}
