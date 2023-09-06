package io.github.edwinmindcraft.apoli.common.condition.biome;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BiomeCondition;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

@Deprecated
public class CategoryBiomeCondition extends BiomeCondition<FieldConfiguration<TagKey<Biome>>> {

    public CategoryBiomeCondition() {
        super(FieldConfiguration.codec(SerializableDataTypes.STRING.xmap(s ->
                TagKey.create(Registries.BIOME, Apoli.identifier("category/" + s)),
                (TagKey<Biome> tagKey) -> tagKey.location().getPath().replaceFirst("category/", "")), "category"));
    }

    @Override
    public boolean check(FieldConfiguration<TagKey<Biome>> configuration, Holder<Biome> biome) {
        return biome.isBound() && biome.is(configuration.value());
    }

}
