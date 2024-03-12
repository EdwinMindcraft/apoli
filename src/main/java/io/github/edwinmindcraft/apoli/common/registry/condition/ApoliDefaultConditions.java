package io.github.edwinmindcraft.apoli.common.registry.condition;

import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.configuration.*;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConstantConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;

import static io.github.edwinmindcraft.apoli.common.registry.ApoliDynamicRegisters.*;

public class ApoliDefaultConditions {
	public static final DeferredHolder<ConfiguredBiEntityCondition<?, ?>, ConfiguredBiEntityCondition<?, ?>> BIENTITY_DEFAULT = CONFIGURED_BIENTITY_CONDITIONS.register(ApoliDynamicRegistries.CONDITION_DEFAULT.getPath(), () -> new ConfiguredBiEntityCondition<>(ApoliBiEntityConditions.CONSTANT, new ConstantConfiguration<>(true), ConditionData.DEFAULT));
	public static final DeferredHolder<ConfiguredBiomeCondition<?, ?>, ConfiguredBiomeCondition<?, ?>> BIOME_DEFAULT = CONFIGURED_BIOME_CONDITIONS.register(ApoliDynamicRegistries.CONDITION_DEFAULT.getPath(), () -> new ConfiguredBiomeCondition<>(ApoliBiomeConditions.CONSTANT, new ConstantConfiguration<>(true), ConditionData.DEFAULT));
	public static final DeferredHolder<ConfiguredBlockCondition<?, ?>, ConfiguredBlockCondition<?, ?>> BLOCK_DEFAULT = CONFIGURED_BLOCK_CONDITIONS.register(ApoliDynamicRegistries.CONDITION_DEFAULT.getPath(), () -> new ConfiguredBlockCondition<>(ApoliBlockConditions.CONSTANT, new ConstantConfiguration<>(true), ConditionData.DEFAULT));
	public static final DeferredHolder<ConfiguredDamageCondition<?, ?>, ConfiguredDamageCondition<?, ?>> DAMAGE_DEFAULT = CONFIGURED_DAMAGE_CONDITIONS.register(ApoliDynamicRegistries.CONDITION_DEFAULT.getPath(), () -> new ConfiguredDamageCondition<>(ApoliDamageConditions.CONSTANT, new ConstantConfiguration<>(true), ConditionData.DEFAULT));
	public static final DeferredHolder<ConfiguredEntityCondition<?, ?>, ConfiguredEntityCondition<?, ?>> ENTITY_DEFAULT = CONFIGURED_ENTITY_CONDITIONS.register(ApoliDynamicRegistries.CONDITION_DEFAULT.getPath(), () -> new ConfiguredEntityCondition<>(ApoliEntityConditions.CONSTANT, new ConstantConfiguration<>(true), ConditionData.DEFAULT));
	public static final DeferredHolder<ConfiguredFluidCondition<?, ?>, ConfiguredFluidCondition<?, ?>> FLUID_DEFAULT = CONFIGURED_FLUID_CONDITIONS.register(ApoliDynamicRegistries.CONDITION_DEFAULT.getPath(), () -> new ConfiguredFluidCondition<>(ApoliFluidConditions.CONSTANT, new ConstantConfiguration<>(true), ConditionData.DEFAULT));
	public static final DeferredHolder<ConfiguredItemCondition<?, ?>, ConfiguredItemCondition<?, ?>> ITEM_DEFAULT = CONFIGURED_ITEM_CONDITIONS.register(ApoliDynamicRegistries.CONDITION_DEFAULT.getPath(), () -> new ConfiguredItemCondition<>(ApoliItemConditions.CONSTANT, new ConstantConfiguration<>(true), ConditionData.DEFAULT));

	public static final DeferredHolder<ConfiguredBiEntityCondition<?, ?>, ConfiguredBiEntityCondition<?, ?>> BIENTITY_DENY = CONFIGURED_BIENTITY_CONDITIONS.register(ApoliDynamicRegistries.DENY.getPath(), () -> new ConfiguredBiEntityCondition<>(ApoliBiEntityConditions.CONSTANT, new ConstantConfiguration<>(false), ConditionData.DEFAULT));
	public static final DeferredHolder<ConfiguredBiomeCondition<?, ?>, ConfiguredBiomeCondition<?, ?>> BIOME_DENY = CONFIGURED_BIOME_CONDITIONS.register(ApoliDynamicRegistries.DENY.getPath(), () -> new ConfiguredBiomeCondition<>(ApoliBiomeConditions.CONSTANT, new ConstantConfiguration<>(false), ConditionData.DEFAULT));
	public static final DeferredHolder<ConfiguredBlockCondition<?, ?>, ConfiguredBlockCondition<?, ?>> BLOCK_DENY = CONFIGURED_BLOCK_CONDITIONS.register(ApoliDynamicRegistries.DENY.getPath(), () -> new ConfiguredBlockCondition<>(ApoliBlockConditions.CONSTANT, new ConstantConfiguration<>(false), ConditionData.DEFAULT));
	public static final DeferredHolder<ConfiguredDamageCondition<?, ?>, ConfiguredDamageCondition<?, ?>> DAMAGE_DENY = CONFIGURED_DAMAGE_CONDITIONS.register(ApoliDynamicRegistries.DENY.getPath(), () -> new ConfiguredDamageCondition<>(ApoliDamageConditions.CONSTANT, new ConstantConfiguration<>(false), ConditionData.DEFAULT));
	public static final DeferredHolder<ConfiguredEntityCondition<?, ?>, ConfiguredEntityCondition<?, ?>> ENTITY_DENY = CONFIGURED_ENTITY_CONDITIONS.register(ApoliDynamicRegistries.DENY.getPath(), () -> new ConfiguredEntityCondition<>(ApoliEntityConditions.CONSTANT, new ConstantConfiguration<>(false), ConditionData.DEFAULT));
	public static final DeferredHolder<ConfiguredFluidCondition<?, ?>, ConfiguredFluidCondition<?, ?>> FLUID_DENY = CONFIGURED_FLUID_CONDITIONS.register(ApoliDynamicRegistries.DENY.getPath(), () -> new ConfiguredFluidCondition<>(ApoliFluidConditions.CONSTANT, new ConstantConfiguration<>(false), ConditionData.DEFAULT));
	public static final DeferredHolder<ConfiguredItemCondition<?, ?>, ConfiguredItemCondition<?, ?>> ITEM_DENY = CONFIGURED_ITEM_CONDITIONS.register(ApoliDynamicRegistries.DENY.getPath(), () -> new ConfiguredItemCondition<>(ApoliItemConditions.CONSTANT, new ConstantConfiguration<>(false), ConditionData.DEFAULT));

	public static void bootstrap() {

	}
}
