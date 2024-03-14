package io.github.edwinmindcraft.apoli.common.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;

public record RemoveEnchantmentConfiguration(ListConfiguration<Enchantment> enchantments, Optional<Integer> levels, boolean resetRepairCost) implements IDynamicFeatureConfiguration {
    public static final Codec<RemoveEnchantmentConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ListConfiguration.mapCodec(SerializableDataTypes.ENCHANTMENT, "enchantment", "enchantments").forGetter(RemoveEnchantmentConfiguration::enchantments),
            ExtraCodecs.strictOptionalField(CalioCodecHelper.INT, "levels").forGetter(RemoveEnchantmentConfiguration::levels),
            ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "reset_repair_cost", false).forGetter(RemoveEnchantmentConfiguration::resetRepairCost)
    ).apply(instance, RemoveEnchantmentConfiguration::new));
}
