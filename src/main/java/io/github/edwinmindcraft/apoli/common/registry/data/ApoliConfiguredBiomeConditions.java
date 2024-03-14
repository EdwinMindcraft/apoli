package io.github.edwinmindcraft.apoli.common.registry.data;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiomeCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConstantConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliBiomeConditions;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

public class ApoliConfiguredBiomeConditions {
	public static ResourceKey<ConfiguredBiomeCondition<?, ?>> ALLOW = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_BIOME_CONDITION_KEY, Apoli.identifier("allow"));
	public static ResourceKey<ConfiguredBiomeCondition<?, ?>> DENY = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_BIOME_CONDITION_KEY, Apoli.identifier("deny"));

	public static void bootstrap(BootstapContext<ConfiguredBiomeCondition<?, ?>> context) {
		context.register(ALLOW, ApoliBiomeConditions.CONSTANT.get().configure(new ConstantConfiguration<>(true)));
		context.register(DENY, ApoliBiomeConditions.CONSTANT.get().configure(new ConstantConfiguration<>(false)));
	}
}
