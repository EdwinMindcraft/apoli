package io.github.edwinmindcraft.apoli.common.registry.data;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConstantConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliBiEntityConditions;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

public class ApoliConfiguredBiEntityConditions {
	public static ResourceKey<ConfiguredBiEntityCondition<?, ?>> ALLOW = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_BIENTITY_CONDITION_KEY, Apoli.identifier("allow"));
	public static ResourceKey<ConfiguredBiEntityCondition<?, ?>> DENY = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_BIENTITY_CONDITION_KEY, Apoli.identifier("deny"));

	public static void bootstrap(BootstapContext<ConfiguredBiEntityCondition<?, ?>> context) {
		context.register(ALLOW, ApoliBiEntityConditions.CONSTANT.get().configure(new ConstantConfiguration<>(true)));
		context.register(DENY, ApoliBiEntityConditions.CONSTANT.get().configure(new ConstantConfiguration<>(false)));
	}
}
