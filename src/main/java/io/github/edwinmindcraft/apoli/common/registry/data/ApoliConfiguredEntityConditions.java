package io.github.edwinmindcraft.apoli.common.registry.data;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredDamageCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConstantConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliDamageConditions;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliEntityConditions;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

public class ApoliConfiguredEntityConditions {
	public static ResourceKey<ConfiguredEntityCondition<?, ?>> ALLOW = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_ENTITY_CONDITION_KEY, Apoli.identifier("allow"));
	public static ResourceKey<ConfiguredEntityCondition<?, ?>> DENY = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_ENTITY_CONDITION_KEY, Apoli.identifier("deny"));

	public static void bootstrap(BootstapContext<ConfiguredEntityCondition<?, ?>> context) {
		context.register(ALLOW, ApoliEntityConditions.CONSTANT.get().configure(new ConstantConfiguration<>(true)));
		context.register(DENY, ApoliEntityConditions.CONSTANT.get().configure(new ConstantConfiguration<>(false)));
	}
}
