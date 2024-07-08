package io.github.apace100.apoli.util;

import com.mojang.serialization.Codec;
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

public class AddPowerLootFunction extends LootItemConditionalFunction {

	public static final MapCodec<AddPowerLootFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> commonFields(inst).and(inst.group(
			EquipmentSlot.CODEC.fieldOf("slot").forGetter(AddPowerLootFunction::getSlot),
			ResourceLocation.CODEC.fieldOf("power").forGetter(AddPowerLootFunction::getPowerId),
			Codec.BOOL.optionalFieldOf("hidden", false).forGetter(AddPowerLootFunction::isHidden),
			Codec.BOOL.optionalFieldOf("negative", false).forGetter(AddPowerLootFunction::isNegative)
	)).apply(inst, AddPowerLootFunction::new));

	private final EquipmentSlot slot;
	private final ResourceLocation powerId;
	private final boolean hidden;
	private final boolean negative;


	private AddPowerLootFunction(List<LootItemCondition> conditions, EquipmentSlot slot, ResourceLocation powerId, boolean hidden, boolean negative) {
		super(conditions);
		this.slot = slot;
		this.powerId = powerId;
		this.hidden = hidden;
		this.negative = negative;
	}

	@NotNull
	public LootItemFunctionType<AddPowerLootFunction> getType() {
		return ApoliLootFunctions.ADD_POWER_LOOT_FUNCTION.get();
	}

	@Override
	@NotNull
	public ItemStack run(@NotNull ItemStack stack, @NotNull LootContext context) {
		StackPowerUtil.addPower(stack, this.slot, this.powerId, this.hidden, this.negative);
		return stack;
	}

	public EquipmentSlot getSlot() {
		return slot;
	}

	public ResourceLocation getPowerId() {
		return powerId;
	}

	public boolean isHidden() {
		return hidden;
	}

	public boolean isNegative() {
		return negative;
	}

	@NotNull
	public static LootItemConditionalFunction.Builder<?> builder(EquipmentSlot slot, ResourceLocation powerId, boolean hidden, boolean negative) {
		return LootItemConditionalFunction.simpleBuilder((conditions) -> new AddPowerLootFunction(conditions, slot, powerId, hidden, negative));
	}
}
