package io.github.edwinmindcraft.apoli.common.util;

import com.google.common.base.Joiner;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.apace100.apoli.util.ApoliResourceConditions;
import io.github.edwinmindcraft.calio.common.registry.CalioDynamicRegistryManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AllNamespacesLoadedCondition implements ICondition
{
    private final String[] namespaces;

    public AllNamespacesLoadedCondition(Collection<String> namespaces) {
        this.namespaces = namespaces.toArray(String[]::new);
    }

    @Override
    public ResourceLocation getID() {
        return ApoliResourceConditions.ALL_NAMESPACES_LOADED;
    }

    @Override
    public boolean test(IContext context) {
        for (String namespace : this.namespaces) {
            if (!CalioDynamicRegistryManager.LOADED_NAMESPACES.contains(namespace))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "apoli:all_namespaces_loaded(\"" + Joiner.on(", ").join(namespaces) + "\")";
    }

    public static class Serializer implements IConditionSerializer<AllNamespacesLoadedCondition> {
        public static final AllNamespacesLoadedCondition.Serializer INSTANCE = new AllNamespacesLoadedCondition.Serializer();

        @Override
        public void write(JsonObject json, AllNamespacesLoadedCondition value) {
            JsonArray values = new JsonArray();
            for (String namespace : value.namespaces)
                values.add(namespace);
            json.add("namespaces", values);
        }

        @Override
        public AllNamespacesLoadedCondition read(JsonObject json) {
            JsonArray array = GsonHelper.getAsJsonArray(json, "namespaces");
            Set<String> namespaces = new HashSet<>();
            for (JsonElement element : array) {
                if (element.isJsonPrimitive()) {
                    namespaces.add(element.getAsString());
                } else {
                    throw new JsonParseException("Invalid " + element + " entry: expected a JSON string!");
                }
            }
            return new AllNamespacesLoadedCondition(namespaces);
        }

        @Override
        public ResourceLocation getID() {
            return ApoliResourceConditions.ALL_NAMESPACES_LOADED;
        }
    }
}
