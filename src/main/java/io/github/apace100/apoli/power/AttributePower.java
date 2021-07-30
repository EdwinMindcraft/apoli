package io.github.apace100.apoli.power;

import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributePower extends Power {

    private final List<AttributedEntityAttributeModifier> modifiers = new LinkedList<AttributedEntityAttributeModifier>();
    private final boolean updateHealth;

    public AttributePower(PowerType<?> type, LivingEntity entity, boolean updateHealth) {
        super(type, entity);
        this.updateHealth = updateHealth;
    }

    public AttributePower(PowerType<?> type, LivingEntity entity, boolean updateHealth, Attribute attribute, AttributeModifier modifier) {
        this(type, entity, updateHealth);
        addModifier(attribute, modifier);
    }

    public AttributePower addModifier(Attribute attribute, AttributeModifier modifier) {
        AttributedEntityAttributeModifier mod = new AttributedEntityAttributeModifier(attribute, modifier);
        this.modifiers.add(mod);
        return this;
    }

    public AttributePower addModifier(AttributedEntityAttributeModifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    @Override
    public void onAdded() {
        if(!entity.level.isClientSide) {
            float previousMaxHealth = entity.getMaxHealth();
            float previousHealthPercent = entity.getHealth() / previousMaxHealth;
            modifiers.forEach(mod -> {
                if(entity.getAttributes().hasAttribute(mod.getAttribute())) {
                    entity.getAttributeInstance(mod.getAttribute()).addTemporaryModifier(mod.getModifier());
                }
            });
            float afterMaxHealth = entity.getMaxHealth();
            if(updateHealth && afterMaxHealth != previousMaxHealth) {
                entity.setHealth(afterMaxHealth * previousHealthPercent);
            }
        }
    }

    @Override
    public void onRemoved() {
        if(!entity.level.isClientSide) {
            float previousMaxHealth = entity.getMaxHealth();
            float previousHealthPercent = entity.getHealth() / previousMaxHealth;
            modifiers.forEach(mod -> {
                if (entity.getAttributes().hasAttribute(mod.getAttribute())) {
                    entity.getAttributeInstance(mod.getAttribute()).removeModifier(mod.getModifier());
                }
            });
            float afterMaxHealth = entity.getMaxHealth();
            if(updateHealth && afterMaxHealth != previousMaxHealth) {
                entity.setHealth(afterMaxHealth * previousHealthPercent);
            }
        }
    }
}
