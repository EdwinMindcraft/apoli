package io.github.edwinmindcraft.apoli.api.power.factory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.github.apace100.apoli.util.NamespaceAlias;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.IFactory;
import io.github.edwinmindcraft.apoli.api.power.ModifierData;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Holder;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModifierOperation {
    public static final Codec<ModifierOperation> STRICT_CODEC = ApoliRegistries.codec(() -> ApoliRegistries.MODIFIER_OPERATION.get());
    public static final Codec<ModifierOperation> CODEC = createCodec();
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
                            if(instance.getData().resource().isPresent()) {
                                LazyOptional<IPowerContainer> lazyOptional = IPowerContainer.get(entity);
                                if (lazyOptional.isPresent() && lazyOptional.resolve().isPresent()) {
                                    IPowerContainer container = lazyOptional.resolve().get();
                                    Optional<ResourceKey<ConfiguredPower<?, ?>>> optionalKey = instance.getData().resource().get().unwrapKey();
                                    if(optionalKey.isEmpty() || !container.hasPower(optionalKey.get())) {
                                        value = instance.getData().value();
                                    } else {
                                        @Nullable Holder<ConfiguredPower<IDynamicFeatureConfiguration, PowerFactory<IDynamicFeatureConfiguration>>> p = container.getPower(optionalKey.get());
                                        if(p != null && p.get().asVariableIntPower().isPresent()) {
                                            value = p.value().asVariableIntPower().get().getValue(p.get(), entity);
                                        }
                                    }
                                }
                            } else {
                                value = instance.getData().value();
                            }
                            if(!instance.getData().modifiers().isEmpty()) {
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

    /**
     * This is essentially a modified version of {@link io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries} with
     * extra parts to decoding to allow compatibility with the old attribute modifiers as well as
     * Origins Fabric's modifier operations, which are registered under Minecraft's namespace.
     *
     * @return The new codec.
     */
    private static Codec<ModifierOperation> createCodec() {
        Supplier<ForgeRegistry<ModifierOperation>> supplier = () -> (ForgeRegistry<ModifierOperation>)ApoliRegistries.MODIFIER_OPERATION.get();
        return new Codec<>() {
            @Override
            public <U> DataResult<Pair<ModifierOperation, U>> decode(DynamicOps<U> dynamicOps, U input) {
                return dynamicOps.compressMaps() ? dynamicOps.getNumberValue(input).flatMap((number) -> {
                    ModifierOperation object = supplier.get().getValue(number.intValue());
                    return object == null ? DataResult.error(() -> "Unknown registry id: " + number) : DataResult.success(object);
                }).map((obj) -> Pair.of(obj, dynamicOps.empty())) : Codec.STRING.decode(dynamicOps, input).flatMap((pair) -> {
                    ResourceLocation location;
                    try {
                        String string = pair.getFirst().toLowerCase(Locale.ROOT);
                        switch (string) {
                            case "addition" -> location = ApoliAPI.identifier("add_base_early");
                            case "multiply_base" -> location = ApoliAPI.identifier("multiply_base_additive");
                            case "multiply_total" -> location = ApoliAPI.identifier("multiply_total_multiplicative");
                            default -> {
                                if (!string.contains(":"))
                                    location = ApoliAPI.identifier(string);
                                else
                                    location = ResourceLocation.of(string, ':');
                            }
                        }
                    } catch (ResourceLocationException ex) {
                        return DataResult.error(() -> "Not a valid resource location: " + pair.getFirst() + " " + ex.getMessage());
                    }
                    ModifierOperation object = supplier.get().getValue(location);
                    if (object == null && NamespaceAlias.hasAlias(location))
                        object = supplier.get().getValue(NamespaceAlias.resolveAlias(location));
                    return object == null ? DataResult.error(() -> "Unknown registry key: " + pair.getFirst()) : DataResult.success(Pair.of(object, pair.getSecond()));
                });
            }

            @Override
            public <T> DataResult<T> encode(ModifierOperation input, DynamicOps<T> ops, T prefix) {
                return STRICT_CODEC.encode(input, ops, prefix);
            }
        };
    }
}
