package io.github.apace100.apoli.util;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.common.registry.ApoliLootFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RemovePowerLootFunction extends LootItemConditionalFunction {

    public static final MapCodec<RemovePowerLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(instance.group(
            EquipmentSlot.CODEC.fieldOf("slot").forGetter(RemovePowerLootFunction::getSlot),
            ResourceLocation.CODEC.fieldOf("power").forGetter(RemovePowerLootFunction::getPowerId)
    )).apply(instance, RemovePowerLootFunction::new));


    private final EquipmentSlot slot;
    private final ResourceLocation powerId;

    private RemovePowerLootFunction(List<LootItemCondition> conditions, EquipmentSlot slot, ResourceLocation powerId) {
        super(conditions);
        this.slot = slot;
        this.powerId = powerId;
    }

    @NotNull
    public LootItemFunctionType<RemovePowerLootFunction> getType() {
        return ApoliLootFunctions.REMOVE_POWER_LOOT_FUNCTION.get();
    }

    @Override
    @NotNull
    public ItemStack run(@NotNull ItemStack stack, @NotNull LootContext context) {
        StackPowerUtil.removePower(stack, slot, powerId);
        return stack;
    }
    public EquipmentSlot getSlot() {
        return slot;
    }

    public ResourceLocation getPowerId() {
        return powerId;
    }

    @NotNull
    public static Builder<?> builder(EquipmentSlot slot, ResourceLocation powerId) {
        return LootItemConditionalFunction.simpleBuilder((conditions) -> new RemovePowerLootFunction(conditions, slot, powerId));
    }
}