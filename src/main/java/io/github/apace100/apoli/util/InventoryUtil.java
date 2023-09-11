package io.github.apace100.apoli.util;

import io.github.apace100.apoli.mixin.ItemSlotArgumentTypeAccessor;
import io.github.apace100.calio.util.ArgumentWrapper;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.InventoryPower;
import io.github.edwinmindcraft.apoli.common.power.configuration.InventoryConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliDefaultConditions;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class InventoryUtil {

    private static final SlotArgument SLOT_ARGUMENT = new SlotArgument();

    public enum InventoryType {
        INVENTORY,
        POWER
    }

    public enum ProcessMode {
        STACKS(stack -> 1),
        ITEMS(ItemStack::getCount);

        private final Function<ItemStack, Integer> processor;

        ProcessMode(Function<ItemStack, Integer> processor) {
            this.processor = processor;
        }

        public Function<ItemStack, Integer> getProcessor() {
            return processor;
        }
    }

    public static Set<Integer> getSlots(ListConfiguration<ArgumentWrapper<Integer>> slotArgumentTypes) {
        Set<Integer> slots = new HashSet<>();

        if (slotArgumentTypes.entries().isEmpty()) {
            Map<String, Integer> slotNamesWithId = ((ItemSlotArgumentTypeAccessor) SLOT_ARGUMENT).getSlotNamesToSlotCommandId();
            slots.addAll(slotNamesWithId.values());
        } else {
            for (ArgumentWrapper<Integer> slotArgumentType : slotArgumentTypes.entries()) {
                slots.add(slotArgumentType.get());
            }
        }

        return slots;
    }


    public static int checkInventory(Holder<ConfiguredItemCondition<?, ?>> itemCondition, ListConfiguration<ArgumentWrapper<Integer>> slotArgumentTypes, Entity entity, ConfiguredPower<InventoryConfiguration, InventoryPower> inventoryPower, Function<ItemStack, Integer> processor) {
        Set<Integer> slots = getSlots(slotArgumentTypes);
        deduplicateSlots(entity, slots);
        int matches = 0;

        if (inventoryPower == null) {
            for (int slot : slots) {

                SlotAccess slotAccess = entity.getSlot(slot);
                if (slotAccess == SlotAccess.NULL) {
                    continue;
                }

                ItemStack stack = slotAccess.get();
                if ((itemCondition.isBound() && itemCondition.is(ApoliDefaultConditions.ITEM_DEFAULT.getId()) && !stack.isEmpty()) || ConfiguredItemCondition.check(itemCondition, entity.level(), stack)) {
                    matches += processor.apply(stack);
                }
            }
        } else {
            for (int slot : slots) {

                if (slot < 0 || slot >= inventoryPower.getFactory().getInventory(inventoryPower, entity).getContainerSize()) {
                    continue;
                }

                ItemStack stack = inventoryPower.getFactory().getInventory(inventoryPower, entity).getItem(slot);
                if ((itemCondition.isBound() && itemCondition.is(ApoliDefaultConditions.ITEM_DEFAULT.getId()) && !stack.isEmpty()) || ConfiguredItemCondition.check(itemCondition, entity.level(), stack)) {
                    matches += processor.apply(stack);
                }
            }
        }

        return matches;
    }

    public static void modifyInventory(ListConfiguration<ArgumentWrapper<Integer>> slotArgumentTypes,
                                       Holder<ConfiguredEntityAction<?, ?>> entityAction,
                                       Holder<ConfiguredItemCondition<?, ?>> itemCondition,
                                       Holder<ConfiguredItemAction<?, ?>> itemAction,
                                       Entity entity, Optional<ResourceLocation> powerId,
                                       Function<ItemStack, Integer> processor, int limit) {
        if(limit <= 0) {
            limit = Integer.MAX_VALUE;
        }

        Set<Integer> slots = getSlots(slotArgumentTypes);
        deduplicateSlots(entity, slots);
        int counter = 0;

        if (powerId.isEmpty()) {
            for (int slot : slots) {
                SlotAccess stackReference = entity.getSlot(slot);
                if (stackReference != SlotAccess.NULL) {
                    ItemStack currentItemStack = stackReference.get();
                    if (!currentItemStack.isEmpty()) {
                        if (ConfiguredItemCondition.check(itemCondition, entity.level(), currentItemStack)) {
                            ConfiguredEntityAction.execute(entityAction, entity);
                            int amount = processor.apply(currentItemStack);
                            for(int i = 0; i < amount; i++) {
                                Mutable<ItemStack> newStack = new MutableObject<>(currentItemStack);
                                ConfiguredItemAction.execute(itemAction, entity.level(), newStack);
                                stackReference.set(newStack.getValue());

                                counter += 1;

                                if(counter >= limit) {
                                    break;
                                }
                            }

                            if(counter >= limit) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        else {
            ConfiguredPower<?, ?> power = IPowerContainer.get(entity).resolve()
                    .map(x -> x.getPower(powerId.get())).map(Holder::value).orElse(null);
            if (power == null || !(power.getFactory() instanceof InventoryPower)) return;
            ConfiguredPower<InventoryConfiguration, InventoryPower> inventoryPower = (ConfiguredPower<InventoryConfiguration, InventoryPower>)power;
            int inventorySize = inventoryPower.getFactory().getSize(inventoryPower, entity);
            slots.removeIf(slot -> slot > inventorySize);
            for (int i = 0; i < inventorySize; i++) {
                if (!slots.isEmpty() && !slots.contains(i)) continue;
                Container container = inventoryPower.getFactory().getInventory(inventoryPower, entity);
                ItemStack currentItemStack = container.getItem(i);
                if (!currentItemStack.isEmpty()) {
                    if (ConfiguredItemCondition.check(itemCondition, entity.level(), currentItemStack)) {
                        ConfiguredEntityAction.execute(entityAction, entity);
                        Mutable<ItemStack> newStack = new MutableObject<>(currentItemStack);
                        ConfiguredItemAction.execute(itemAction, entity.level(), newStack);
                        container.setItem(i, newStack.getValue());
                    }
                }
            }
        }

    }

    public static void replaceInventory(ListConfiguration<ArgumentWrapper<Integer>> slotArgumentTypes,
                                        ItemStack replacementStack,
                                        Holder<ConfiguredEntityAction<?, ?>> entityAction,
                                        Holder<ConfiguredItemCondition<?, ?>> itemCondition,
                                        Holder<ConfiguredItemAction<?, ?>> itemAction,
                                        Entity entity, Optional<ResourceLocation> powerId,
                                        boolean mergeNbt) {

        Set<Integer> slots = getSlots(slotArgumentTypes);
        deduplicateSlots(entity, slots);

        if (powerId.isEmpty()) {
            for (Integer slot : slots) {
                SlotAccess stackReference = entity.getSlot(slot);
                if (stackReference != SlotAccess.NULL) {
                    ItemStack currentItemStack = stackReference.get();
                    if (ConfiguredItemCondition.check(itemCondition, entity.level(), currentItemStack)) {
                        ConfiguredEntityAction.execute(entityAction, entity);
                        Mutable<ItemStack> newStack = new MutableObject<>(replacementStack.copy());
                        if (mergeNbt && newStack.getValue().hasTag())
                            newStack.getValue().getOrCreateTag().merge(replacementStack.getOrCreateTag());
                        ConfiguredItemAction.execute(itemAction, entity.level(), newStack);
                        stackReference.set(newStack.getValue());
                    }
                }
            }
        }

        else {
            ConfiguredPower<?, ?> power = IPowerContainer.get(entity).resolve()
                    .map(x -> x.getPower(powerId.get())).map(Holder::value).orElse(null);
            if (power == null || !(power.getFactory() instanceof InventoryPower)) return;
            ConfiguredPower<InventoryConfiguration, InventoryPower> inventoryPower = (ConfiguredPower<InventoryConfiguration, InventoryPower>)power;
            int inventorySize = inventoryPower.getFactory().getSize(inventoryPower, entity);
            slots.removeIf(slot -> slot > inventorySize);
            for (int i = 0; i < inventorySize; i++) {
                if (!slots.isEmpty() && !slots.contains(i)) continue;
                Container container = inventoryPower.getFactory().getInventory(inventoryPower, entity);
                ItemStack currentItemStack = container.getItem(i);
                if (ConfiguredItemCondition.check(itemCondition, entity.level(), currentItemStack)) {
                    ConfiguredEntityAction.execute(entityAction, entity);
                    Mutable<ItemStack> newStack = new MutableObject<>(replacementStack.copy());
                    if (mergeNbt && newStack.getValue().hasTag())
                        newStack.getValue().getOrCreateTag().merge(replacementStack.getOrCreateTag());
                    ConfiguredItemAction.execute(itemAction, entity.level(), newStack);
                    container.setItem(i, newStack.getValue());
                }
            }
        }

    }

    public static void dropInventory(ListConfiguration<ArgumentWrapper<Integer>> slotArgumentTypes,
                                     Holder<ConfiguredEntityAction<?, ?>> entityAction,
                                     Holder<ConfiguredItemCondition<?, ?>> itemCondition,
                                     Holder<ConfiguredItemAction<?, ?>> itemAction,
                                     boolean throwRandomly, boolean retainOwnership,
                                     Entity entity, Optional<ResourceLocation> powerId,
                                     int amount) {

        Set<Integer> slots = getSlots(slotArgumentTypes);
        deduplicateSlots(entity, slots);

        if (powerId.isEmpty()) {
            for (Integer slot : slots) {
                SlotAccess stackReference = entity.getSlot(slot);
                if (stackReference != SlotAccess.NULL) {
                    ItemStack currentItemStack = stackReference.get();
                    if (!currentItemStack.isEmpty()) {
                        if (ConfiguredItemCondition.check(itemCondition, entity.level(), currentItemStack)) {
                            ConfiguredEntityAction.execute(entityAction, entity);
                            Mutable<ItemStack> newStack = new MutableObject<>(currentItemStack.copy());
                            ConfiguredItemAction.execute(itemAction, entity.level(), newStack);
                            if (amount != 0) {
                                int newAmount = amount > 0 ? amount * -1 : amount;
                                newStack.setValue(newStack.getValue().split(newAmount));
                                stackReference.set(newStack.getValue());
                            } else
                                stackReference.set(ItemStack.EMPTY);
                            throwItem(entity, newStack.getValue(), throwRandomly, retainOwnership);
                        }
                    }
                }
            }
        }

        else {
            ConfiguredPower<?, ?> power = IPowerContainer.get(entity).resolve()
                    .map(x -> x.getPower(powerId.get())).map(Holder::value).orElse(null);
            if (power == null || !(power.getFactory() instanceof InventoryPower)) return;
            ConfiguredPower<InventoryConfiguration, InventoryPower> inventoryPower = (ConfiguredPower<InventoryConfiguration, InventoryPower>)power;
            int containerSize = inventoryPower.getFactory().getSize(inventoryPower, entity);
            slots.removeIf(slot -> slot > containerSize);
            for (int i = 0; i < containerSize; i++) {
                if (!slots.isEmpty() && !slots.contains(i)) continue;
                Container container = inventoryPower.getFactory().getInventory(inventoryPower, entity);
                ItemStack currentItemStack = container.getItem(i);
                if (!currentItemStack.isEmpty()) {
                    if (ConfiguredItemCondition.check(itemCondition, entity.level(), currentItemStack)) {
                        ConfiguredEntityAction.execute(entityAction, entity);
                        Mutable<ItemStack> newStack = new MutableObject<>(currentItemStack.copy());
                        ConfiguredItemAction.execute(itemAction, entity.level(), newStack);
                        if (amount != 0) {
                            int newAmount = amount > 0 ? amount * -1 : amount;
                            newStack.setValue(newStack.getValue().split(newAmount));
                            container.setItem(i, newStack.getValue());
                        } else
                            container.setItem(i, ItemStack.EMPTY);
                        throwItem(entity, newStack.getValue(), throwRandomly, retainOwnership);
                    }
                }
            }
        }

    }

    public static void throwItem(Entity thrower, ItemStack itemStack, boolean throwRandomly, boolean retainOwnership) {

        if (itemStack.isEmpty()) return;
        if (thrower instanceof Player playerEntity && playerEntity.level().isClientSide) playerEntity.swing(InteractionHand.MAIN_HAND);

        double yOffset = thrower.getEyeY() - 0.30000001192092896D;
        ItemEntity itemEntity = new ItemEntity(thrower.level(), thrower.getX(), yOffset, thrower.getZ(), itemStack);
        itemEntity.setPickUpDelay(40);

        Random random = new Random();

        float f;
        float g;

        if (retainOwnership) itemEntity.setThrower(thrower.getUUID());
        if (throwRandomly) {
            f = random.nextFloat() * 0.5F;
            g = random.nextFloat() * 6.2831855F;
            itemEntity.setDeltaMovement(- Mth.sin(g) * f, 0.20000000298023224D, Mth.cos(g) * f);
        }
        else {
            f = 0.3F;
            g = Mth.sin(thrower.getXRot() * 0.017453292F);
            float h = Mth.cos(thrower.getXRot() * 0.017453292F);
            float i = Mth.sin(thrower.getYRot() * 0.017453292F);
            float j = Mth.cos(thrower.getYRot() * 0.017453292F);
            float k = random.nextFloat() * 6.2831855F;
            float l = 0.02F * random.nextFloat();
            itemEntity.setDeltaMovement(
                (double) (- i * h * f) + Math.cos(k) * (double) l,
                -g * f + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F,
                (double) (j * h * f) + Math.sin(k) * (double) l
            );
        }

        thrower.level().addFreshEntity(itemEntity);

    }

    /*
    Includes the optimisations done in https://github.com/apace100/apoli/pull/132 prior to it releasing in Origins Fabric.
    There's no way I would've let the unoptimised version of this be present...

    This method is not supposed to be able to set ItemStacks as you usually would through a Mutable<ItemStack>, mainly as it is not needed for base Apoli usage.
     */
    public static void forEachStack(Entity entity, Consumer<ItemStack> itemStackConsumer) {
        int skip = getDuplicatedSlotIndex(entity);

        for(int slot : ((ItemSlotArgumentTypeAccessor) SLOT_ARGUMENT).getSlotNamesToSlotCommandId().values()) {
            if (slot == skip) {
                skip = Integer.MIN_VALUE;
                continue;
            }
            SlotAccess stackReference = entity.getSlot(slot);
            if (stackReference == SlotAccess.NULL) continue;

            ItemStack itemStack = stackReference.get();
            if (itemStack.isEmpty()) continue;
            itemStackConsumer.accept(itemStack);
        }

        Optional<IPowerContainer> optionalPowerContainer = IPowerContainer.get(entity).resolve();
        if(optionalPowerContainer.isPresent()) {
            IPowerContainer phc = optionalPowerContainer.get();
            List<ConfiguredPower<InventoryConfiguration, InventoryPower>> inventoryPowers = phc.getPowers(ApoliPowers.INVENTORY.get()).stream().filter(Holder::isBound).map(Holder::value).toList();
            for(ConfiguredPower<InventoryConfiguration, InventoryPower> inventoryPower : inventoryPowers) {
                int inventorySize = inventoryPower.getFactory().getSize(inventoryPower, entity);
                for(int index = 0; index < inventorySize; index++) {
                    ItemStack stack = inventoryPower.getFactory().getInventory(inventoryPower, entity).getItem(index);
                    if(stack.isEmpty()) {
                        continue;
                    }
                    itemStackConsumer.accept(stack);
                }
            }
        }
    }


    /*
    Includes the optimisations done in https://github.com/apace100/apoli/pull/132 prior to it releasing in Origins Fabric.
    There's no way I would've let the unoptimised version of this be present...
     */
    private static void deduplicateSlots(Entity entity, Set<Integer> slots) {
        int hotbarSlot = getDuplicatedSlotIndex(entity);
        if(hotbarSlot != Integer.MIN_VALUE && slots.contains(hotbarSlot)) {
            Integer mainHandSlot = ((ItemSlotArgumentTypeAccessor) SLOT_ARGUMENT).getSlotNamesToSlotCommandId().get("weapon.mainhand");
            slots.remove(mainHandSlot);
        }
    }

    private static int getDuplicatedSlotIndex(Entity entity) {
        if(entity instanceof Player player) {
            int selectedSlot = player.getInventory().selected;
            return ((ItemSlotArgumentTypeAccessor) SLOT_ARGUMENT).getSlotNamesToSlotCommandId().get("hotbar." + selectedSlot);
        }
        return Integer.MIN_VALUE;
    }
}