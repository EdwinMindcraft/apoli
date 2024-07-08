package io.github.edwinmindcraft.apoli.common.util;

import com.google.common.base.Joiner;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.calio.common.registry.CalioDynamicRegistryManagerImpl;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.Arrays;

public record AnyNamespacesLoadedCondition(String[] namespaces) implements ICondition {
    public static final MapCodec<AnyNamespacesLoadedCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.listOf().fieldOf("namespaces").xmap(l -> l.toArray(String[]::new), Arrays::asList).forGetter(AnyNamespacesLoadedCondition::namespaces)
    ).apply(inst, AnyNamespacesLoadedCondition::new));

    @Override
    public boolean test(IContext context) {
        for (String namespace : this.namespaces) {
            if (CalioDynamicRegistryManagerImpl.LOADED_NAMESPACES.contains(namespace))
                return true;
        }
        return false;
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public String toString() {
        return "apoli:any_namespace_loaded(\"" + Joiner.on(", ").join(namespaces) + "\")";
    }
}
