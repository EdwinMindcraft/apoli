package io.github.apace100.apoli.power;

import io.github.apace100.apoli.util.HudRender;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SelfActionWhenHitPower extends CooldownPower {

    private final Predicate<Tuple<DamageSource, Float>> damageCondition;
    private final Consumer<Entity> entityAction;

    public SelfActionWhenHitPower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, Predicate<Tuple<DamageSource, Float>> damageCondition, Consumer<Entity> entityAction) {
        super(type, entity, cooldownDuration, hudRender);
        this.damageCondition = damageCondition;
        this.entityAction = entityAction;
    }

    public void whenHit(DamageSource damageSource, float damageAmount) {
        if(damageCondition == null || damageCondition.test(new Tuple<>(damageSource, damageAmount))) {
            if(canUse()) {
                this.entityAction.accept(this.entity);
                use();
            }
        }
    }
}
