package io.github.edwinmindcraft.apoli.common.registry.condition;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.MetaFactories;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredFluidCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.FluidCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.apoli.common.condition.fluid.DelegatedFluidCondition;
import io.github.edwinmindcraft.apoli.common.condition.fluid.FluidMatchesCondition;
import io.github.edwinmindcraft.apoli.common.condition.fluid.InTagFluidCondition;
import io.github.edwinmindcraft.apoli.common.condition.fluid.SimpleFluidCondition;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConditionStreamConfiguration;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConstantConfiguration;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Arrays;
import java.util.function.BiPredicate;

import static io.github.edwinmindcraft.apoli.common.registry.ApoliRegisters.FLUID_CONDITIONS;

public class ApoliFluidConditions {
	public static final BiPredicate<ConfiguredFluidCondition<?, ?>, FluidState> PREDICATE = ConfiguredFluidCondition::check;

	private static <U extends FluidCondition<?>> DeferredHolder<FluidCondition<?>, U> of(String name) {
		return DeferredHolder.create(ApoliRegistries.FLUID_CONDITION_KEY, Apoli.identifier(name));
	}

	public static final DeferredHolder<FluidCondition<?>, DelegatedFluidCondition<ConstantConfiguration<FluidState>>> CONSTANT = of("constant");
	public static final DeferredHolder<FluidCondition<?>, DelegatedFluidCondition<ConditionStreamConfiguration<ConfiguredFluidCondition<?, ?>, FluidState>>> AND = of("and");
	public static final DeferredHolder<FluidCondition<?>, DelegatedFluidCondition<ConditionStreamConfiguration<ConfiguredFluidCondition<?, ?>, FluidState>>> OR = of("or");

	public static final DeferredHolder<FluidCondition<?>, SimpleFluidCondition> EMPTY = FLUID_CONDITIONS.register("empty", () -> new SimpleFluidCondition(FluidState::isEmpty));
	public static final DeferredHolder<FluidCondition<?>, SimpleFluidCondition> STILL = FLUID_CONDITIONS.register("still", () -> new SimpleFluidCondition(FluidState::isSource));
	public static final DeferredHolder<FluidCondition<?>, InTagFluidCondition> IN_TAG = FLUID_CONDITIONS.register("in_tag", InTagFluidCondition::new);
	public static final DeferredHolder<FluidCondition<?>, FluidMatchesCondition> FLUID = FLUID_CONDITIONS.register("fluid", FluidMatchesCondition::new);

	public static ConfiguredFluidCondition<?, ?> constant(boolean value) {
		return CONSTANT.get().configure(new ConstantConfiguration<>(value));
	}

	@SafeVarargs
	public static ConfiguredFluidCondition<?, ?> and(HolderSet<ConfiguredFluidCondition<?, ?>>... conditions) {
		return AND.get().configure(ConditionStreamConfiguration.and(Arrays.asList(conditions), PREDICATE));
	}

	@SafeVarargs
	public static ConfiguredFluidCondition<?, ?> or(HolderSet<ConfiguredFluidCondition<?, ?>>... conditions) {
		return OR.get().configure(ConditionStreamConfiguration.or(Arrays.asList(conditions), PREDICATE));
	}

	public static void bootstrap() {
		MetaFactories.defineMetaConditions(FLUID_CONDITIONS, DelegatedFluidCondition::new, ConfiguredFluidCondition.CODEC_SET, PREDICATE);
	}
}
