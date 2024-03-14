package io.github.edwinmindcraft.apoli.common.registry.data;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredDamageCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConstantConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliBlockConditions;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliDamageConditions;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

public class ApoliConfiguredDamageConditions {
	public static ResourceKey<ConfiguredDamageCondition<?, ?>> ALLOW = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_DAMAGE_CONDITION_KEY, Apoli.identifier("allow"));
	public static ResourceKey<ConfiguredDamageCondition<?, ?>> DENY = ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_DAMAGE_CONDITION_KEY, Apoli.identifier("deny"));

	public static void bootstrap(BootstapContext<ConfiguredDamageCondition<?, ?>> context) {
		context.register(ALLOW, ApoliDamageConditions.CONSTANT.get().configure(new ConstantConfiguration<>(true)));
		context.register(DENY, ApoliDamageConditions.CONSTANT.get().configure(new ConstantConfiguration<>(false)));
	}
}
