package io.github.edwinmindcraft.apoli.common.registry.data;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredFluidCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConstantConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliEntityConditions;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliFluidConditions;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

public class ApoliConfiguredFluidConditions {
	public static ResourceKey<ConfiguredFluidCondition<?, ?>> ALLOW = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_FLUID_CONDITION_KEY, Apoli.identifier("allow"));
	public static ResourceKey<ConfiguredFluidCondition<?, ?>> DENY = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_FLUID_CONDITION_KEY, Apoli.identifier("deny"));

	public static void bootstrap(BootstapContext<ConfiguredFluidCondition<?, ?>> context) {
		context.register(ALLOW, ApoliFluidConditions.CONSTANT.get().configure(new ConstantConfiguration<>(true)));
		context.register(DENY, ApoliFluidConditions.CONSTANT.get().configure(new ConstantConfiguration<>(false)));
	}
}
