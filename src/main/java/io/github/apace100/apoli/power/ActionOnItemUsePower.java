package io.github.apace100.apoli.power;

import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ActionOnItemUsePower extends Power {

    private final Predicate<ItemStack> itemCondition;
    private final Consumer<Entity> entityAction;
    private final Consumer<ItemStack> itemAction;

    public ActionOnItemUsePower(PowerType<?> type, LivingEntity entity, Predicate<ItemStack> itemCondition, Consumer<Entity> entityAction, Consumer<ItemStack> itemAction) {
        super(type, entity);
        this.itemCondition = itemCondition;
        this.entityAction = entityAction;
        this.itemAction = itemAction;
    }

    public boolean doesApply(ItemStack stack) {
        return itemCondition == null || itemCondition.test(stack);
    }

    public void executeActions(ItemStack stack) {
        if(itemAction != null) {
            itemAction.accept(stack);
        }
        if(entityAction != null) {
            entityAction.accept(entity);
        }
    }
}
