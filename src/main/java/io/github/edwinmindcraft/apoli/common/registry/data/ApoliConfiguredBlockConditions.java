package io.github.edwinmindcraft.apoli.common.registry.data;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConstantConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliBlockConditions;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

public class ApoliConfiguredBlockConditions {
	public static ResourceKey<ConfiguredBlockCondition<?, ?>> ALLOW = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_BLOCK_CONDITION_KEY, Apoli.identifier("allow"));
	public static ResourceKey<ConfiguredBlockCondition<?, ?>> DENY = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_BLOCK_CONDITION_KEY, Apoli.identifier("deny"));

	public static void bootstrap(BootstapContext<ConfiguredBlockCondition<?, ?>> context) {
		context.register(ALLOW, ApoliBlockConditions.CONSTANT.get().configure(new ConstantConfiguration<>(true)));
		context.register(DENY, ApoliBlockConditions.CONSTANT.get().configure(new ConstantConfiguration<>(false)));
	}
}
