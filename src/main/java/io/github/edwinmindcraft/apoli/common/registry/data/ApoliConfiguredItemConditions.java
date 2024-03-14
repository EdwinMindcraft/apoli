package io.github.edwinmindcraft.apoli.common.registry.data;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredFluidCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConstantConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliFluidConditions;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliItemConditions;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

public class ApoliConfiguredItemConditions {
	public static ResourceKey<ConfiguredItemCondition<?, ?>> ALLOW = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_ITEM_CONDITION_KEY, Apoli.identifier("allow"));
	public static ResourceKey<ConfiguredItemCondition<?, ?>> DENY = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_ITEM_CONDITION_KEY, Apoli.identifier("deny"));

	public static void bootstrap(BootstapContext<ConfiguredItemCondition<?, ?>> context) {
		context.register(ALLOW, ApoliItemConditions.CONSTANT.get().configure(new ConstantConfiguration<>(true)));
		context.register(DENY, ApoliItemConditions.CONSTANT.get().configure(new ConstantConfiguration<>(false)));
	}
}
