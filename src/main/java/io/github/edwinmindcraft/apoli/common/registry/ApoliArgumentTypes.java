package io.github.edwinmindcraft.apoli.common.registry;

import io.github.apace100.apoli.command.*;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;

import java.util.function.Supplier;

public class ApoliArgumentTypes {

	public static final Supplier<ArgumentTypeInfo<PowerTypeArgumentType, ?>> POWER_TYPE = ApoliRegisters.ARGUMENT_TYPES.register("power", () -> SingletonArgumentInfo.contextFree(PowerTypeArgumentType::power));
    public static final Supplier<ArgumentTypeInfo<PowerSourceArgumentType, ?>> POWER_SOURCE = ApoliRegisters.ARGUMENT_TYPES.register("power_source", () -> SingletonArgumentInfo.contextAware(PowerSourceArgumentType::powerSource));
	public static final Supplier<ArgumentTypeInfo<PowerOperation, ?>> POWER_OPERATION = ApoliRegisters.ARGUMENT_TYPES.register("power_operation", () -> SingletonArgumentInfo.contextFree(PowerOperation::operation));
	public static final Supplier<ArgumentTypeInfo<EntityConditionArgument, ?>> ENTITY_CONDITION = ApoliRegisters.ARGUMENT_TYPES.register("entity_condition", () -> SingletonArgumentInfo.contextFree(EntityConditionArgument::entityCondition));

	public static void bootstrap() {}

	public static void initialize() {
		ArgumentTypeInfos.registerByClass(PowerTypeArgumentType.class, POWER_TYPE.get());
		ArgumentTypeInfos.registerByClass(PowerSourceArgumentType.class, POWER_SOURCE.get());
		ArgumentTypeInfos.registerByClass(PowerOperation.class, POWER_OPERATION.get());
		ArgumentTypeInfos.registerByClass(EntityConditionArgument.class, ENTITY_CONDITION.get());
	}
}
