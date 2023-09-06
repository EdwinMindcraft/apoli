package io.github.edwinmindcraft.apoli.common.condition.damage;

import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class SimpleTagCondition extends SimpleDamageCondition {
    public SimpleTagCondition(TagKey<DamageType> tagKey) {
        super((source, aFloat) -> source.is(tagKey));
    }
}
