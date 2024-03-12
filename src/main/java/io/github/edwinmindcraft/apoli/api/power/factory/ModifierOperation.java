package io.github.edwinmindcraft.apoli.api.power.factory;

import com.mojang.serialization.Codec;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.PowerContainer;
import io.github.edwinmindcraft.apoli.api.power.IFactory;
import io.github.edwinmindcraft.apoli.api.power.ModifierData;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.core.Holder;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ModifierOperation {
	public static final Codec<ModifierOperation> CODEC = ApoliRegistries.MODIFIER_OPERATION.byNameCodec();
	private final Codec<ConfiguredModifier<?>> codec;

	private final Phase phase;
	private final int order;
	private final PropertyDispatch.TriFunction<List<Double>, Double, Double, Double> function;

	public ModifierOperation(Phase phase, int order, PropertyDispatch.TriFunction<List<Double>, Double, Double, Double> function) {
		this.codec = IFactory.singleCodec(ModifierData.CODEC, data -> new ConfiguredModifier<>(() -> this, data), ConfiguredModifier::getData);
		this.phase = phase;
		this.order = order;
		this.function = function;
	}

	public Codec<ConfiguredModifier<?>> getCodec() {
		return this.codec;
	}

	/**
	 * Specifies which value is modified by this modifier, either
	 * the base value or the total. All Phase.BASE modifiers will run before
	 * the first Phase.TOTAL modifier.
	 *
	 * @return
	 */
	public Phase getPhase() {
		return phase;
	}

	/**
	 * Specifies when this modifier is applied, related to other modifiers in the same phase.
	 * Higher number means it applies later.
	 *
	 * @return order value
	 */
	public int getOrder() {
		return order;
	}

	public double apply(List<ConfiguredModifier<?>> instances, Entity entity, double base, double current) {
		return function.apply(
				instances.stream()
						.map(instance -> {
							double value = 0;
							if (instance.getData().resource().isPresent()) {
								@Nullable PowerContainer container = PowerContainer.get(entity);
								if (container != null) {
									Optional<ResourceKey<ConfiguredPower<?, ?>>> optionalKey = instance.getData().resource().get().unwrapKey();
									if (optionalKey.isEmpty() || !container.hasPower(optionalKey.get())) {
										value = instance.getData().value();
									} else {
										@Nullable Holder<ConfiguredPower<IDynamicFeatureConfiguration, PowerFactory<IDynamicFeatureConfiguration>>> p = container.getPower(optionalKey.get());
										if (p != null && p.value().asVariableIntPower().isPresent()) {
											value = p.value().asVariableIntPower().get().getValue(p.value(), entity);
										}
									}
								}
							} else {
								value = instance.getData().value();
							}
							if (!instance.getData().modifiers().isEmpty()) {
								List<ConfiguredModifier<?>> modifiers = instance.getData().modifiers();
								value = ModifierUtil.applyModifiers(entity, modifiers, value);
							}
							return value;
						})
						.collect(Collectors.toList()),
				base, current);
	}

	public enum Phase {
		BASE, TOTAL
	}
}
