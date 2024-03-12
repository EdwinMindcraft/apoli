package io.github.edwinmindcraft.apoli.common.registry;

import io.github.edwinmindcraft.apoli.api.power.factory.ModifierOperation;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.apoli.common.modifier.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ApoliModifierOperations {

	public static final DeferredHolder<ModifierOperation, AddBaseEarlyModifierOperation> ADD_BASE_EARLY = ApoliRegisters.MODIFIER_OPERATIONS.register("add_base_early", AddBaseEarlyModifierOperation::new);
	public static final DeferredHolder<ModifierOperation, AddBaseLateModifierOperation> ADD_BASE_LATE = ApoliRegisters.MODIFIER_OPERATIONS.register("add_base_late", AddBaseLateModifierOperation::new);
	public static final DeferredHolder<ModifierOperation, AddTotalLateModifierOperation> ADD_TOTAL_LATE = ApoliRegisters.MODIFIER_OPERATIONS.register("add_total_late", AddTotalLateModifierOperation::new);
	public static final DeferredHolder<ModifierOperation, MaxBaseModifierOperation> MAX_BASE = ApoliRegisters.MODIFIER_OPERATIONS.register("max_base", MaxBaseModifierOperation::new);
	public static final DeferredHolder<ModifierOperation, MaxTotalModifierOperation> MAX_TOTAL = ApoliRegisters.MODIFIER_OPERATIONS.register("max_total", MaxTotalModifierOperation::new);
	public static final DeferredHolder<ModifierOperation, MinBaseModifierOperation> MIN_BASE = ApoliRegisters.MODIFIER_OPERATIONS.register("min_base", MinBaseModifierOperation::new);
	public static final DeferredHolder<ModifierOperation, MinTotalModifierOperation> MIN_TOTAL = ApoliRegisters.MODIFIER_OPERATIONS.register("min_total", MinTotalModifierOperation::new);
	public static final DeferredHolder<ModifierOperation, MultiplyBaseAdditiveModifierOperation> MULTIPLY_BASE_ADDITIVE = ApoliRegisters.MODIFIER_OPERATIONS.register("multiply_base_additive", MultiplyBaseAdditiveModifierOperation::new);
	public static final DeferredHolder<ModifierOperation, MultiplyBaseMultiplicativeModifierOperation> MULTIPLY_BASE_MULTIPLICATIVE = ApoliRegisters.MODIFIER_OPERATIONS.register("multiply_base_multiplicative", MultiplyBaseMultiplicativeModifierOperation::new);
	public static final DeferredHolder<ModifierOperation, MultiplyTotalAdditiveModifierOperation> MULTIPLY_TOTAL_ADDITIVE = ApoliRegisters.MODIFIER_OPERATIONS.register("multiply_total_additive", MultiplyTotalAdditiveModifierOperation::new);
	public static final DeferredHolder<ModifierOperation, MultiplyTotalMultiplicativeModifierOperation> MULTIPLY_TOTAL_MULTIPLICATIVE = ApoliRegisters.MODIFIER_OPERATIONS.register("multiply_total_multiplicative", MultiplyTotalMultiplicativeModifierOperation::new);
	public static final DeferredHolder<ModifierOperation, SetBaseModifierOperation> SET_BASE = ApoliRegisters.MODIFIER_OPERATIONS.register("set_base", SetBaseModifierOperation::new);
	public static final DeferredHolder<ModifierOperation, SetTotalModifierOperation> SET_TOTAL = ApoliRegisters.MODIFIER_OPERATIONS.register("set_total", SetTotalModifierOperation::new);

	public static void bootstrap() {
		ApoliRegistries.MODIFIER_OPERATION.addAlias(new ResourceLocation("addition"), ADD_BASE_EARLY.getId());
		ApoliRegistries.MODIFIER_OPERATION.addAlias(new ResourceLocation("multiply_base"), MULTIPLY_BASE_ADDITIVE.getId());
		ApoliRegistries.MODIFIER_OPERATION.addAlias(new ResourceLocation("multiply_total"), MULTIPLY_TOTAL_ADDITIVE.getId());
	}
}
