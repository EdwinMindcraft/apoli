package io.github.edwinmindcraft.apoli.common.condition.damage;

import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.configuration.FieldConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.DamageCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public class InTagCondition extends DamageCondition<FieldConfiguration<TagKey<DamageType>>> {

    public InTagCondition() {
        super(FieldConfiguration.codec(SerializableDataType.tag(Registries.DAMAGE_TYPE), "tag"));
    }

    @Override
    protected boolean check(FieldConfiguration<TagKey<DamageType>> configuration, DamageSource source, float amount) {
        return source.is(configuration.value());
    }
}
